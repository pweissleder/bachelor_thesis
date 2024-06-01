package ba.pascal.weissleder.server.services.framework

import ba.pascal.weissleder.server.controllers.framework.ContextController
import ba.pascal.weissleder.server.model.framework.Context
import ba.pascal.weissleder.server.model.framework.User
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.repositories.UserRepository
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,

    @Lazy
    private val contextController: ContextController
) {
    fun updateUser(user: User): User {
        return userRepository.saveAndFlush(user)
    }

    fun deleteUser(id: Long) {
        userRepository.deleteById(id)
    }

    fun getUserById(id: Long): User? {
        return userRepository.findById(id).get()
    }

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    // USer Context Mapping
    fun userHasContext(userId: Long, contextId: Long): Boolean {
        return userRepository.getReferenceById(userId).contexts.map { it.id }.contains(contextId)
    }

    fun addContextToUser(userId: Long, contextId: Long) {
        val user = userRepository.getReferenceById(userId)
        val context = contextController.getContext(contextId).body ?: throw NoSuchElementException()
        user.contexts.add(context)
        userRepository.saveAndFlush(user)
    }

    fun removeContextFromUser(userId: Long, contextId: Long) {
        val user = userRepository.getReferenceById(userId)
        val context = user.contexts.find { it.id == contextId } ?: throw NoSuchElementException()
        user.contexts.remove(context)
        userRepository.saveAndFlush(user)
    }


    // User Device Mapping
    fun getUserDevices(userId: Long): List<NodeID> {
        return userRepository.getReferenceById(userId).contexts.flatMap { it.devices }
    }

    fun userHasDevice(userId: Long, deviceID: NodeID): Boolean {  // TODO Her noch problem
        return userRepository.getReferenceById(userId)
            .contexts.flatMap { context: Context -> context.devices }
            .contains(deviceID)
    }

    fun findByContextsContaining(context: Context): List<User> {
        return userRepository.findByContextsContaining(context)
    }


}