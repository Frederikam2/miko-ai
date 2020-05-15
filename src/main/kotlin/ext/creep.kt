package ext

import memory.room
import screeps.api.Creep
import screeps.api.Game
import screeps.api.Memory
import screeps.api.Room
import screeps.api.RoomMemory
import screeps.api.get

val Creep.homeRoom get(): Room? = Game.rooms[memory.room]
val Creep.homeRoomMemory get(): RoomMemory? = Memory.rooms[memory.room]
