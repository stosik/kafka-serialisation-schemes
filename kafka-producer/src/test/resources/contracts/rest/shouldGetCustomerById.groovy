package contracts.rest

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

Contract.make {
    name "get customer by id"
    description "REST endpoint to retrieve customer by id"

    request {
        url "/customers/1"
        method GET()
    }

    response {
        status OK()
        headers {
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
        body([id: "1", name: "Chris", surname: "Froome"])
    }
}