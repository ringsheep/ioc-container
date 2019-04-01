import com.zinyakov.ioccontainer.models.Container
import com.zinyakov.ioccontainer.models.LifeCycle
import mocks.Astronaut
import mocks.Engine
import mocks.Spaceship
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class ContainerTests {

    def container = new Container()

    @Test
    void "should create instance and wire all it's dependencies"() {
        container.register(Spaceship.class)
        container.register(Astronaut.class)
        container.register(Engine.class)

        def spaceship = container.resolve(Spaceship.class)

        assert spaceship != null
        assert spaceship.pilot != null
        assert spaceship.engine != null
    }

    @Test
    void "should not recreate instance when it's registered in singleton mode"() {
        container.register(Spaceship.class, LifeCycle.SINGLETON)
        container.register(Astronaut.class)
        container.register(Engine.class)

        def spaceship = container.resolve(Spaceship.class)

        assert spaceship == container.resolve(Spaceship.class)
        assert spaceship.pilot != container.resolve(Astronaut.class)
        assert spaceship.engine != container.resolve(Engine.class)
    }

    @Test
    void "should fail when some dependencies are not registered"() {
        container.register(Spaceship.class)

        shouldFail(IllegalArgumentException) {
            container.resolve(Spaceship.class)
        }
    }

}
