import com.google.inject.AbstractModule
import javax.inject.Singleton

@Singleton
class StartupModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[StartupComponent]).to(classOf[StartupComponentImpl]).asEagerSingleton()
  }

}
