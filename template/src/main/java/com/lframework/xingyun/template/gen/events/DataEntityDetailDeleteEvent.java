package com.lframework.xingyun.template.gen.events;

import org.springframework.context.ApplicationEvent;

public class DataEntityDetailDeleteEvent extends ApplicationEvent {

  /**
   * 数据实体明细ID
   */
  private String id;

  /**
   * 显示名称
   */
  private String name;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with which the event is
   *               associated (never {@code null})
   */
  public DataEntityDetailDeleteEvent(Object source) {

    super(source);
  }

  public String getId() {

    return id;
  }

  public void setId(String id) {

    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
