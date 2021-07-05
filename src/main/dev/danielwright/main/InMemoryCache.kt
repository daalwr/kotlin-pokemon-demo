package dev.danielwright.main

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

// https://henrycourse.com/2020/04/30/async-caching-in-ktor-with-a-minor-detour/
private val cacheContext = CoroutineScope(Dispatchers.Default + SupervisorJob())
private val cacheMap: MutableMap<KClass<suspend (Any?) -> Any?>, AsyncLoadingCache<*, *>> = HashMap()
suspend fun <K, V> cached(key: K,
                          cacheConfig: Caffeine<Any, Any>.() -> Caffeine<Any, Any> = {
                              this.maximumSize(10_000)
                                  .expireAfterWrite(3, TimeUnit.HOURS)
                          },
                          loader: suspend (K) -> V): V = withContext(cacheContext.coroutineContext) {
    val cacheMap = cacheMap as MutableMap<KClass<out suspend (K) -> V>, AsyncLoadingCache<K, V>>
    (cacheMap[loader::class] ?: Caffeine.newBuilder()
        .cacheConfig()
        .buildAsync { key: K, _ -> cacheContext.future { loader(key) } }
        .also { cacheMap[loader::class] = it })
        .get(key)
        .await()
}