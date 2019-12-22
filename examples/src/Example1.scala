package com.gurghet.example
import zio.stream.ZStream
import zio.UIO
import breeze.optimize.StochasticGradientDescent
import breeze.linalg.DenseVector
import breeze.linalg._
import breeze.optimize._
import zio.ZIO
import scala.util.Random
import zio.console._
import zio.App
import zio.ZEnv
import zio.duration._

object Main extends App {
  val initialSolution = 5d
  val rng: Random = {
    val mutableRandom = new Random()
    mutableRandom.setSeed(initialSolution.toLong)
    mutableRandom
  }
  val h: Double => Double = (x: Double) => x * x
  val neighborhoodCount = 1
  final case class Neighborhood(centre: Double, structure: Int) {
    def randomNeighbor: Double = {
      centre + (rng.nextDouble() - 0.5d)
    }
  }
  def nextNeighborhood(
      incumbentSolution: Double,
      newSolution: Double,
      neighborhoodId: Int
  ): Neighborhood = {
    val nextNeighborhood = neighborhoodId + 1 % neighborhoodCount
    if (h(newSolution) < h(incumbentSolution)) {
      Neighborhood(newSolution, neighborhoodId)
    } else {
      Neighborhood(incumbentSolution, nextNeighborhood)
    }
  }

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    ZStream
      .fromIterable(LazyList.from(0))
      .mapAccum(Neighborhood(initialSolution, 0)) { (neighborhood, index) =>
        // shake phase
        val newSolution = neighborhood.randomNeighbor
        // change neighborhood
        val newNeighborhood =
          nextNeighborhood(
            neighborhood.centre,
            newSolution,
            neighborhood.structure
          )
        (newNeighborhood, newNeighborhood.centre)
      }
      .tap(solution => putStrLn("sol: " + solution.toString))
      .take(9000000)
      .runDrain
      .map(_ => 0)
}
