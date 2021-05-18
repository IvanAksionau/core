package core.testcore

import core.BaseConfiguration
import core.asserts.Asserts
import core.asserts.IAsserts
import core.config.env.BaseEnvironment
import core.context.IContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.AfterTest

import java.util.logging.Logger

@ContextConfiguration(classes = [BaseConfiguration])
class BaseTest extends AbstractTestNGSpringContextTests {

    @Autowired
    protected Logger log

    @Autowired
    protected IContext testContext

    @Autowired
    private Asserts asserts

    private BaseEnvironment baseEnvironment

    @AfterTest()
    void cleanContext() {
        testContext?.clear()
    }

    @Autowired
    protected void setBaseEnvironment(Optional<BaseEnvironment> baseEnvironment) {
        this.baseEnvironment = baseEnvironment.orElse(new BaseEnvironment())
    }

    protected BaseEnvironment getBaseEnvironment() {
        baseEnvironment
    }

    protected IAsserts getAsserts() {
        asserts
    }
}
