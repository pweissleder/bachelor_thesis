package ba.pascal.weissleder.server.services.framework

import ba.pascal.weissleder.server.controllers.framework.UserController
import ba.pascal.weissleder.server.model.framework.Capability
import ba.pascal.weissleder.server.model.framework.Context
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.repositories.ContextRepository
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class ContextService(
    private val contextRepository: ContextRepository,

    @Lazy
    private val userController: UserController
) {
    //CRUD
    fun getContextById(id: Long): Context? {
        return contextRepository.findById(id).get()
    }

    fun getAllContexts(): List<Context> {
        return contextRepository.findAll()
    }

    fun createContext(context: Context): Context {
        return contextRepository.save(context)
    }

    fun deleteContext(id: Long) {
        val context = contextRepository.findById(id).orElseThrow { NoSuchElementException() }

        // Remove context from the subContexts list of any other contexts that reference it
        val referencingContexts = contextRepository.findBySubContextsContaining(context)
        for (referencingContext in referencingContexts) {
            referencingContext.subContexts.remove(context)
            contextRepository.saveAndFlush(referencingContext)
        }
        // Remove  context from the contexts list of any users that reference it
        val referencingUsers = userController.findByContextsContaining(context)
        for (user in referencingUsers) {
            user.contexts.remove(context)
            userController.updateUser(user)
        }

        contextRepository.delete(context)
    }


    // Permissions
    fun contextHasInvocationPermission(contextId: Long): Boolean {
        val capability = getCapabilityContext(contextId)
        return when (capability) {
            Capability.R -> false
            Capability.RI -> true
            Capability.RW -> false
            Capability.RIW -> true
            else -> false
        }
    }

    fun contextHasReadPermission(contextId: Long): Boolean {
        val capability = getCapabilityContext(contextId)
        return when (capability) {
            Capability.R -> true
            Capability.RI -> true
            Capability.RW -> true
            Capability.RIW -> true
            else -> false
        }
    }

    fun contextHasWritePermission(contextId: Long): Boolean {
        val capability = getCapabilityContext(contextId)
        return when (capability) {
            Capability.R -> false
            Capability.RI -> false
            Capability.RW -> true
            Capability.RIW -> true
            else -> false
        }
    }

    fun getCapabilityContext(contextId: Long): Capability? {
        return contextRepository.getReferenceById(contextId).capability
    }


    //Context Device Mapping
    fun contextHasDevice(contextId: Long, deviceId: NodeID): Boolean {
        return contextRepository.getReferenceById(contextId).devices.any { it == deviceId }
    }

    fun getContextDevices(contextId: Long): List<NodeID> {       // tested - works
        return contextRepository.getReferenceById(contextId).devices
    }

    fun getAllDevicesInContextAndSubContexts(contextId: Long): List<NodeID> {   // tested - works
        val devices = mutableSetOf<NodeID>()
        val context = contextRepository.getReferenceById(contextId)
        devices.addAll(context.devices)
        for (subContext in context.subContexts) {
            devices.addAll(getAllDevicesInContextAndSubContexts(subContext.id ?: 0)) // never context with id 0
        }
        return devices.toList()
    }

    fun addDeviceToContext(contextId: Long, deviceId: NodeID) {    // tested - works
        val context = contextRepository.getReferenceById(contextId)
        context.devices.add(deviceId)
        contextRepository.saveAndFlush(context)
    }


    fun deleteDeviceFromContext(contextId: Long, deviceId: NodeID) { // tested - works
        val context = contextRepository.getReferenceById(contextId)
        context.devices.remove(deviceId)
        contextRepository.saveAndFlush(context)
    }

    // Subcontext Mapping
    fun addSubContext(contextId: Long, subContextId: Long) {
        val context = contextRepository.getReferenceById(contextId)
        val subContext = contextRepository.getReferenceById(subContextId)
        context.subContexts.add(subContext)
        contextRepository.saveAndFlush(context)
    }

    fun removeSubContext(contextId: Long, subContextId: Long) {
        val context = contextRepository.getReferenceById(contextId)
        val subContext = contextRepository.getReferenceById(subContextId)
        context.subContexts.remove(subContext)
        contextRepository.saveAndFlush(context)
    }


}