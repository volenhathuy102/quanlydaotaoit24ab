package com.example.quanlydaotao.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class Room extends AuditedEntity {

    @Column(name = "room_code")
    private String roomCode;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "building_id")
    private UUID buildingId;

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public String getCode() { return roomCode; }
    public void setCode(String code) { this.roomCode = code; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getName() { return roomName; }
    public void setName(String name) { this.roomName = name; }
    public UUID getBuildingId() { return buildingId; }
    public void setBuildingId(UUID buildingId) { this.buildingId = buildingId; }
}
