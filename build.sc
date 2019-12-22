import mill._, scalalib._

trait VnsModule extends ScalaModule {
  def scalaVersion = "2.13.0"
}

object core extends VnsModule {
  val zioVersion = "1.0.0-RC17"
  override def ivyDeps = Agg(
    ivy"dev.zio::zio:$zioVersion",
    ivy"dev.zio::zio-streams:$zioVersion",
    ivy"org.scalanlp::breeze:1.0"
  )
}

object examples extends VnsModule {
  override def moduleDeps = Seq(core)
}