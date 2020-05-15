package ext

import memory.room
import screeps.api.Creep
import screeps.api.Game
import screeps.api.Room
import screeps.api.get

val Creep.homeRoom get(): Room? = Game.rooms[memory.room]