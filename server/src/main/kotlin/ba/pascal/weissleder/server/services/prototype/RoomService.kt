package ba.pascal.weissleder.server.services.prototype

import ba.pascal.weissleder.server.controllers.framework.ContextController
import ba.pascal.weissleder.server.model.prototype.Room
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val contextController: ContextController,
) {
    //CRUD
    fun getRoomById(id: Long): Room? {
        val context = contextController.getContext(id).body ?: throw Exception("Contexts not found")
        return context as Room?
    }

    fun getAllRooms(): List<Room> {
        val contexts = contextController.getAllContexts().body ?: throw Exception("Contexts not found")
        return contexts.filter { it::class == Room::class }.map { it as Room }
    }

    fun createRoom(context: Room): Room {
        return contextController.createContext(context).body as Room
    }

    fun deleteRoom(id: Long) {
        contextController.deleteContext(id)
    }
}