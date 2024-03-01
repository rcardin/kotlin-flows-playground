package `in`.rcard.kotlin.flows

import `in`.rcard.kotlin.flows.Model.Actor
import `in`.rcard.kotlin.flows.Model.FirstName
import `in`.rcard.kotlin.flows.Model.Id
import `in`.rcard.kotlin.flows.Model.LastName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

object Model {
    @JvmInline value class Id(val id: Int)

    @JvmInline value class FirstName(val firstName: String)

    @JvmInline value class LastName(val lastName: String)

    data class Actor(val id: Id, val firstName: FirstName, val lastName: LastName)
}

// Zack Snyder's Justice League
val henryCavill = Actor(Id(1), FirstName("Henry"), LastName("Cavill"))
val galGodot: Actor = Actor(Id(1), FirstName("Gal"), LastName("Godot"))
val ezraMiller: Actor = Actor(Id(2), FirstName("Ezra"), LastName("Miller"))
val benFisher: Actor = Actor(Id(3), FirstName("Ben"), LastName("Fisher"))
val rayHardy: Actor = Actor(Id(4), FirstName("Ray"), LastName("Hardy"))
val jasonMomoa: Actor = Actor(Id(5), FirstName("Jason"), LastName("Momoa"))

// The Avengers
val robertDowneyJr: Actor = Actor(Id(6), FirstName("Robert"), LastName("Downey Jr."))
val chrisEvans: Actor = Actor(Id(7), FirstName("Chris"), LastName("Evans"))
val markRuffalo: Actor = Actor(Id(8), FirstName("Mark"), LastName("Ruffalo"))
val chrisHemsworth: Actor = Actor(Id(9), FirstName("Chris"), LastName("Hemsworth"))
val scarlettJohansson: Actor = Actor(Id(10), FirstName("Scarlett"), LastName("Johansson"))
val jeremyRenner: Actor = Actor(Id(11), FirstName("Jeremy"), LastName("Renner"))

// Spider Man
val tomHolland: Actor = Actor(Id(12), FirstName("Tom"), LastName("Holland"))
val tobeyMaguire: Actor = Actor(Id(13), FirstName("Tobey"), LastName("Maguire"))
val andrewGarfield: Actor = Actor(Id(14), FirstName("Andrew"), LastName("Garfield"))

fun interface FlowCollector<T> {
    suspend fun emit(value: T)
}

interface Flow<T> {
    suspend fun collect(collector: FlowCollector<T>)
}

fun <T> flow(builder: suspend FlowCollector<T>.() -> Unit): `in`.rcard.kotlin.flows.Flow<T> =
    object : `in`.rcard.kotlin.flows.Flow<T> {
        override suspend fun collect(collector: FlowCollector<T>) {
            builder(collector)
        }
    }

suspend fun main() {
    val zackSnyderJusticeLeague: Flow<Actor> =
        flowOf(
            henryCavill,
            galGodot,
            ezraMiller,
            benFisher,
            rayHardy,
            jasonMomoa,
        )

    val avengers: Flow<Actor> =
        listOf(
            robertDowneyJr,
            chrisEvans,
            markRuffalo,
            chrisHemsworth,
            scarlettJohansson,
            jeremyRenner,
        )
            .asFlow()

    val theMostRecentSpiderManFun: () -> Actor = { tomHolland }

    val theMostRecentSpiderMan: Flow<Actor> = theMostRecentSpiderManFun.asFlow()

    val spiderMen: Flow<Actor> =
        flow {
            emit(tobeyMaguire)
            emit(andrewGarfield)
            emit(tomHolland)
        }

    val infiniteJLFlowActors: Flow<Actor> =
        flow {
            while (true) {
                emit(henryCavill)
                emit(galGodot)
                emit(ezraMiller)
                emit(benFisher)
                emit(rayHardy)
                emit(jasonMomoa)
            }
        }

    //    zackSnyderJusticeLeague.collect { println(it) }
//    println("After Zack Snyder's Justice League")
}
