package `in`.rcard.kotlin.flows

import `in`.rcard.kotlin.flows.Model.Actor
import `in`.rcard.kotlin.flows.Model.FirstName
import `in`.rcard.kotlin.flows.Model.Id
import `in`.rcard.kotlin.flows.Model.LastName
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.take

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

fun <T, R> Flow<T>.map(transform: suspend (value: T) -> R): Flow<R> =
    flow {
        this@map.collect { value -> emit(transform(value)) }
    }

fun <T> Flow<T>.filter(predicate: suspend (value: T) -> Boolean): Flow<T> =
    flow {
        this@filter.collect { value ->
            if (predicate(value)) {
                emit(value)
            }
        }
    }

interface ActorRepository {
    suspend fun findJLAActors(): Flow<Actor>
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

    val numberOfJlaActors: Int =
        zackSnyderJusticeLeague.fold(0) { currentNumOfActors, actor -> currentNumOfActors + 1 }

    val numberOfJlaActors_v2: Int = zackSnyderJusticeLeague.count()

    //    val avengers: Flow<Actor> =
    //        listOf(
    //            robertDowneyJr,
    //            chrisEvans,
    //            markRuffalo,
    //            chrisHemsworth,
    //            scarlettJohansson,
    //            jeremyRenner,
    //        )
    //            .asFlow()
    //
    //    val theMostRecentSpiderManFun: () -> Actor = { tomHolland }
    //
    //    val theMostRecentSpiderMan: Flow<Actor> = theMostRecentSpiderManFun.asFlow()
    //
    //    val spiderMen: Flow<Actor> =
    //        flow {
    //            emit(tobeyMaguire)
    //            emit(andrewGarfield)
    //            emit(tomHolland)
    //        }
    //
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

    infiniteJLFlowActors
        .onEach { delay(1000) }
        .scan(0) { currentNumOfActors, actor -> currentNumOfActors + 1 }
        .collect { println(it) }

    infiniteJLFlowActors.take(3)
    infiniteJLFlowActors.drop(3)

    //
    //    val lastNameOfJLActors: Flow<LastName> = zackSnyderJusticeLeague.map { it.lastName }
    //
    //    val lastNameOfJLActors5CharsLong: Flow<LastName> =
    //        lastNameOfJLActors.filter { it.lastName.length == 5 }
    //
    //    val lastNameOfJLActors5CharsLong_v2: Flow<LastName> =
    //        zackSnyderJusticeLeague.mapNotNull {
    //            if (it.lastName.lastName.length == 5) {
    //                it.lastName
    //            } else {
    //                null
    //            }
    //        }
    //    withContext(CoroutineName("Main")) {
    //        coroutineScope {
    //            val delayedJusticeLeague: Flow<Actor> =
    //                flow {
    //                    println("${currentCoroutineContext()[CoroutineName]?.name} - In the flow")
    //                    delay(1000)
    //                    emit(henryCavill)
    //                    delay(1000)
    //                    emit(galGodot)
    //                    delay(1000)
    //                    emit(ezraMiller)
    //                    delay(1000)
    //                    emit(benFisher)
    //                    delay(1000)
    //                    emit(rayHardy)
    //                    delay(1000)
    //                    emit(jasonMomoa)
    //                }
    //
    //            println(
    //                "${currentCoroutineContext()[CoroutineName]?.name} - Before Zack Snyder's
    // Justice League",
    //            )
    //
    //            delayedJusticeLeague.flowOn(CoroutineName("Zack Snyder's Justice League")).collect {
    //                println(it)
    //            }
    //
    //            println(
    //                "${currentCoroutineContext()[CoroutineName]?.name} - After Zack Snyder's Justice
    // League",
    //            )
    //        }
    //    }

    //    val actorRepository: ActorRepository =
    //        object : ActorRepository {
    //            override suspend fun findJLAActors(): Flow<Actor> =
    //                flowOf(
    //                    henryCavill,
    //                    galGodot,
    //                    ezraMiller,
    //                    benFisher,
    //                    rayHardy,
    //                    jasonMomoa,
    //                )
    //        }
    //
    //    actorRepository.findJLAActors().flowOn(Dispatchers.IO).collect { actor -> println(actor) }
}
