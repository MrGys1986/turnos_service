package com.uteq.turnos.turnos_service.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class Notifier {
  private final SimpMessagingTemplate ws;
  public Notifier(SimpMessagingTemplate ws){ this.ws = ws; }

  public void monitor(Object payload){ ws.convertAndSend("/topic/monitor", payload); }
  public void docente(Long docenteId, Object payload){ ws.convertAndSend("/topic/docente/" + docenteId, payload); }
  public void alumno(Long alumnoId, Object payload){ ws.convertAndSend("/topic/alumno/" + alumnoId, payload); }

  public void broadcastChange(String type, Object data){
    var msg = Map.of("type", type, "data", data, "ts", System.currentTimeMillis());
    monitor(msg);
    if (data instanceof Map<?,?> m) {
      Object did = m.get("docenteId"); if (did instanceof Number n) docente(n.longValue(), msg);
      Object aid = m.get("alumnoId");  if (aid instanceof Number n) alumno(n.longValue(), msg);
    }
  }
}
