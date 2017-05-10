import ratpack.groovy.test.embed.GroovyEmbeddedApp
import ratpack.test.ApplicationUnderTest
import ratpack.test.MainClassApplicationUnderTest
import ratpack.test.embed.EmbeddedApp
import ratpack.test.http.TestHttpClient
import spock.lang.Shared
import spock.lang.Specification

class ReverseProxySpec extends Specification {
    @Shared
    ApplicationUnderTest aut = new MainClassApplicationUnderTest(App)

    TestHttpClient client = aut.httpClient

    @Shared
    EmbeddedApp proxiedHost = GroovyEmbeddedApp.of {
        handlers {
            all {
                render "rendered ${request.rawUri}"
            }
        }
    }

    def setupSpec() {
        System.setProperty('ratpack.proxyConfig.host', proxiedHost.address.host)
        System.setProperty('ratpack.proxyConfig.port', Integer.toString(proxiedHost.address.port))
        System.setProperty('ratpack.proxyConfig.scheme', proxiedHost.address.scheme)
    }

    def "get request to ratpack is proxied to the embedded app"() {
        expect:
        client.getText(url) == "rendered /${url}"

        where:
        url << ["", "api", "about"]
    }
}