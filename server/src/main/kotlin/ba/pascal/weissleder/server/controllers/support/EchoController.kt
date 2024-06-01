package ba.pascal.weissleder.server.controllers.support

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/echo")
class EchoController {
    @GetMapping
    fun index(@RequestParam("name") name: String) = " Server Echo for $name!"
}