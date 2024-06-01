package ba.pascal.weissleder.server.repositories


import ba.pascal.weissleder.server.model.framework.Context
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ContextRepository : JpaRepository<Context, Long> {

    @Query("SELECT c FROM Context c WHERE :subContext MEMBER OF c.subContexts")
    fun findBySubContextsContaining(@Param("subContext") subContext: Context): List<Context>



}