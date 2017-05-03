package launch


import org.apache.catalina.core.StandardContext
import org.apache.catalina.startup.Tomcat
import org.apache.catalina.webresources.DirResourceSet
import org.apache.catalina.webresources.StandardRoot
import java.io.File

object Main {
    @JvmStatic fun main(args: Array<String>) {

        val webappDirLocation = "src/main/webapp/"
        val tomcat = Tomcat()

        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        var webPort: String? = System.getenv("PORT")
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080"
        }

        tomcat.setPort(Integer.valueOf(webPort)!!)

        val ctx = tomcat.addWebapp("/", File(webappDirLocation).absolutePath) as StandardContext
        println("configuring app with basedir: " + File("./" + webappDirLocation).absolutePath)

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        val additionWebInfClasses = File("target/classes")
        val resources = StandardRoot(ctx)
        resources.addPreResources(DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.absolutePath, "/"))
        ctx.resources = resources

        tomcat.start()
        tomcat.server.await()
    }
}
