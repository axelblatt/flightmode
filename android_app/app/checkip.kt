import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class checkip() {
    var url: String
    init {
        try {
            URL("http://192.168.1.24:8000/status").readText()
            this.url = "http://192.168.1.24:8000/"
        }
        finally {
            this.url = "http://192.168.31.196:8000/"
        }
    }
}