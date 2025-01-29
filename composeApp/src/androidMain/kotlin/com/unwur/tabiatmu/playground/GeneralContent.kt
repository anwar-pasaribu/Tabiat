package com.unwur.tabiatmu.playground

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language
import ui.theme.MyAppTheme
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
@Preview
fun NotificationPermissionStatusCardPreview(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    MyAppTheme {
        ui.component.card.NotificationPermissionStatusCard()
    }
}

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.4f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "infinite-shimmer")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(3000), repeatMode = RepeatMode.Restart
            ), label = ""
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
private fun HomeLoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .background(shimmerBrush(targetValue = 1300f, showShimmer = false)),
            )
        }
    }
}

val Coral = Color(0xFFF3A397)
val LightYellow = Color(0xFFF8EE94)

@Language("AGSL")
val CUSTOM_SHADER = """
    uniform float2 time;
    uniform float2 resolution;
    layout(color) uniform half4 color;
    layout(color) uniform half4 color2;

    half4 main(in float2 fragCoord) {
        float2 uv = fragCoord/resolution.xy;

        float mixValue = distance(uv, vec2(0, 1)) + abs(sin(time* 0.5));
        return mix(color, color2, mixValue);
    }
""".trimIndent()

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShaderBrushExample() {

    val time by produceState(0F) {
        while (true) {
            withInfiniteAnimationFrameMillis {
                value = it / 1_000F
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,

        ) {
        Text(
            modifier = Modifier
                .graphicsLayer {
                    alpha = .99F
                }
                .drawWithCache {
                    val shader = RuntimeShader(CUSTOM_SHADER)
                    val shaderBrush = ShaderBrush(shader)
                    shader.setFloatUniform("resolution", size.width, size.height)
                    onDrawWithContent {
                        drawContent()
                        shader.setFloatUniform("time", time)
                        shader.setColorUniform(
                            "color",
                            android.graphics.Color.valueOf(
                                LightYellow.red, LightYellow.green,
                                LightYellow
                                    .blue,
                                LightYellow.alpha
                            )
                        )
                        shader.setColorUniform(
                            "color2",
                            android.graphics.Color.valueOf(
                                Coral.red,
                                Coral.green,
                                Coral.blue,
                                Coral.alpha
                            )
                        )
                        drawRect(shaderBrush, blendMode = BlendMode.SrcAtop)
                    }
                },
            text = "ABCDE",
            fontSize = 128.sp,
            fontWeight = FontWeight.ExtraBold,
            style = TextStyle.Default.copy(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black.copy(alpha = .85F),
                    offset = Offset(4.0f, 4.0f),
                    blurRadius = 8f
                )
            )
        )
    }
}

@Composable
fun SkeletonIndicatorPreview() {
    MyAppTheme {
        HomeLoadingIndicator()
    }
}

@Language("AGSL")
val PERLIN_NOISE = """
    uniform float2 resolution;
    uniform float time;
    uniform shader contents; 
    
    //
    // Description : Array and textureless GLSL 2D/3D/4D simplex 
    //               noise functions.
    //      Author : Ian McEwan, Ashima Arts.
    //  Maintainer : stegu
    //     Lastmod : 20201014 (stegu)
    //     License : Copyright (C) 2011 Ashima Arts. All rights reserved.
    //               Distributed under the MIT License. See LICENSE file.
    //               https://github.com/ashima/webgl-noise
    //               https://github.com/stegu/webgl-noise
    // 
    
    vec3 mod289(vec3 x) {
      return x - floor(x * (1.0 / 289.0)) * 289.0;
    }
    
    vec4 mod289(vec4 x) {
      return x - floor(x * (1.0 / 289.0)) * 289.0;
    }
    
    vec4 permute(vec4 x) {
         return mod289(((x*34.0)+10.0)*x);
    }
    
    float snoise(vec3 v)
    { 
      const vec2  C = vec2(1.0/6.0, 1.0/3.0) ;
      const vec4  D = vec4(0.0, 0.5, 1.0, 2.0);
    
       // First corner
      vec3 i  = floor(v + dot(v, C.yyy) );
      vec3 x0 =   v - i + dot(i, C.xxx) ;
    
      // Other corners
      vec3 g = step(x0.yzx, x0.xyz);
      vec3 l = 1.0 - g;
      vec3 i1 = min( g.xyz, l.zxy );
      vec3 i2 = max( g.xyz, l.zxy );
    
      //   x0 = x0 - 0.0 + 0.0 * C.xxx;
      //   x1 = x0 - i1  + 1.0 * C.xxx;
      //   x2 = x0 - i2  + 2.0 * C.xxx;
      //   x3 = x0 - 1.0 + 3.0 * C.xxx;
      vec3 x1 = x0 - i1 + C.xxx;
      vec3 x2 = x0 - i2 + C.yyy; // 2.0*C.x = 1/3 = C.y
      vec3 x3 = x0 - D.yyy;      // -1.0+3.0*C.x = -0.5 = -D.y
    
      // Permutations
      i = mod289(i); 
      vec4 p = permute( permute( permute( 
                 i.z + vec4(0.0, i1.z, i2.z, 1.0 ))
               + i.y + vec4(0.0, i1.y, i2.y, 1.0 )) 
               + i.x + vec4(0.0, i1.x, i2.x, 1.0 ));
    
      // Gradients: 7x7 points over a square, mapped onto an octahedron.
      // The ring size 17*17 = 289 is close to a multiple of 49 (49*6 = 294)
      float n_ = 0.142857142857; // 1.0/7.0
      vec3  ns = n_ * D.wyz - D.xzx;
    
      vec4 j = p - 49.0 * floor(p * ns.z * ns.z);  //  mod(p,7*7)
    
      vec4 x_ = floor(j * ns.z);
      vec4 y_ = floor(j - 7.0 * x_ );    // mod(j,N)
    
      vec4 x = x_ *ns.x + ns.yyyy;
      vec4 y = y_ *ns.x + ns.yyyy;
      vec4 h = 1.0 - abs(x) - abs(y);
    
      vec4 b0 = vec4( x.xy, y.xy );
      vec4 b1 = vec4( x.zw, y.zw );
    
      vec4 s0 = floor(b0)*2.0 + 1.0;
      vec4 s1 = floor(b1)*2.0 + 1.0;
      vec4 sh = -step(h, vec4(0.0));
    
      vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy ;
      vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww ;
    
      vec3 p0 = vec3(a0.xy,h.x);
      vec3 p1 = vec3(a0.zw,h.y);
      vec3 p2 = vec3(a1.xy,h.z);
      vec3 p3 = vec3(a1.zw,h.w);
    
      //Normalise gradients
      vec4 norm = inversesqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3)));
      p0 *= norm.x;
      p1 *= norm.y;
      p2 *= norm.z;
      p3 *= norm.w;
    
      // Mix final noise value
      vec4 m = max(0.5 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0);
      m = m * m;
      return 105.0 * dot( m*m, vec4( dot(p0,x0), dot(p1,x1), 
                                    dot(p2,x2), dot(p3,x3) ) );
    }
    
    half4 main(in vec2 fragCoord) {
        vec2 uv = (fragCoord.xy / resolution.xy);
        float noise = snoise(vec3(uv.x * 6, uv.y * 6, time * 0.5));
        
        noise *= exp(-length(abs(uv * 1.5)));	
        vec2 offset1 = vec2(noise * 0.02);
        vec2 offset2 = vec2(0.02) / resolution.xy;
        uv += offset1 - offset2;
        
        return contents.eval(uv * resolution.xy);
    }
""".trimIndent()

val testEmojis =
    persistentListOf("🍇", "🍅", "🥬", "🍞", "🧀", "🥚", "🥩", "🍫", "🍕", "🍷", "🧃", "🧼", "🧻", "🧴", "🍏")


// For Android. Forgot why I had to do this 🙃
//actual fun getPlatformTextStyle(): PlatformTextStyle = PlatformTextStyle(
//    emojiSupportMatch = EmojiSupportMatch.None
//)
//expect fun getPlatformTextStyle(): PlatformTextStyle

/**
 * Draws concentric rings of emojis around the center of the viewport.
 */
@Composable
fun EmojiBg(
    modifier: Modifier = Modifier,
    emojiBgState: EmojiBgState = rememberEmojiBgState(emojis = testEmojis),
    emojiSize: Dp,
    emojiColor: Color,
    gap: Dp,
) {
    val tm = rememberTextMeasurer()
    val density = LocalDensity.current

    val textSize = density.run {
        ((emojiSize / 2) * sqrt(2f)).toSp() // square in circle
    }

    val textStyle = TextStyle(
        fontSize = textSize,
        color = emojiColor,
    )

    emojiBgState.update(
        itemDiameterPx = density.run { (emojiSize + gap / 2).toPx() }.toInt(),
        textSizePx = density.run { emojiSize.toPx() }.toInt()
    )

    Box(
        modifier
            .fillMaxSize()
            .onSizeChanged {
                emojiBgState.updateContainerSize(
                    containerSize = it
                )
            }
            .drawWithCache {
                val center = size.div(2f)

                onDrawBehind {
                    emojiBgState.items.forEach { item ->
                        val rotAnimatable = emojiBgState.getRotAnimatable(item.layer)
                        val scaleAnimatable = emojiBgState.getScaleAnimatable(item.layer)

                        val left = item.pos.x.toFloat()
                        val top = item.pos.y.toFloat()

                        withTransform({
                            translate(
                                left = left + center.width,
                                top = top + center.height,
                            )

                            rotate(
                                degrees = item.emojiPointer * 87f + rotAnimatable.value,
                                pivot = item.center
                            )

                            scale(
                                scaleX = scaleAnimatable.value,
                                scaleY = scaleAnimatable.value,
                                pivot = item.center
                            )
                        }) {
                            drawText(
                                tm,
                                emojiBgState.getEmojiForPointer(item.emojiPointer),
                                style = textStyle
                            )
                        }
                    }
                }
            }
    )
}

@Composable
fun rememberEmojiBgState(
    emojis: ImmutableList<String>,
): EmojiBgState {
    val scope = rememberCoroutineScope()
    return remember {
        EmojiBgState(
            scope = scope,
            initialEmojis = emojis
        )
    }
}

@Stable
class EmojiBgState(
    val scope: CoroutineScope,
    initialEmojis: List<String>,
) {
    private var emojis: List<String> by mutableStateOf(initialEmojis)

    private var itemDiameterPx by mutableIntStateOf(0)
    private var textSizePx by mutableIntStateOf(0)
    private var containerSize by mutableStateOf(IntSize.Zero)

    /**
     * Number of concentric rings around the center. Updates when container or emoji size changes.
     */
    private val layerCount by derivedStateOf(structuralEqualityPolicy()) {
        ceil(
            (sqrt(
                containerSize.width
                    .toDouble()
                    .pow(2.0) + containerSize.height
                    .toDouble()
                    .pow(2.0)
            ) / 2) / itemDiameterPx
        ).toInt() + 1
    }

    /**
     * Given [layerCount], lays out all items. Ignores container size changes as long as [layerCount] doesn't change.
     */
    private val allItems by derivedStateOf {
        val itemCenter = IntOffset(textSizePx / 2, textSizePx / 2)

        buildList {
            var emojiPointer = 0

            for (layer in 0 until layerCount) {
                val itemsInLayer = (layer * 6).coerceAtLeast(1)
                for (indexInLayer in 0 until itemsInLayer) {
                    val angle = 2 * PI * indexInLayer / itemsInLayer + 8 * layer
                    val distance = layer * itemDiameterPx
                    val x = (distance * cos(angle)).toInt() - itemCenter.x
                    val y = (distance * sin(angle)).toInt() - itemCenter.y

                    // Emoji is picked by taking the next emoji from this.emojis. Since items are filtered later on,
                    // we have to assign a fix number to every item, so that it always picks the same emoji, even when one or more
                    // predecessor are filtered out.
                    val currentPointer = emojiPointer++

                    Item(
                        emojiPointer = currentPointer,
                        pos = IntOffset(x, y),
                        layer = layer,
                        indexInLayer = indexInLayer,
                        size = IntSize(textSizePx, textSizePx)
                    ).also { add(it) }
                }
            }
        }
    }

    /**
     * Contains only items that are currently visible.
     */
    val items: List<Item>
        get() {
            return allItems.filter {
                (-itemDiameterPx..containerSize.width).contains(containerSize.width / 2 + it.pos.x) &&
                        (-itemDiameterPx..containerSize.height).contains(containerSize.height / 2 + it.pos.y)
            }
        }

    /**
     * For animations
     */
    private val layerToPointers: Map<Int, List<Int>>
        get() = allItems.groupBy { it.layer }.mapValues { it.value.map { it.emojiPointer } }

    /**
     * Caches the shown emoji for a given pointer. When changing emojis,
     * the cached emoji is removed and since [getEmojiForPointer] is observed, creates the next
     * emoji right away. Makes it possible to change the emojis in a wave, layer by layer
     */
    private val pointerToEmoji = mutableStateMapOf<Int, String>()
    fun getEmojiForPointer(pointer: Int) = pointerToEmoji.getOrPut(pointer) {
        emojis.size.takeIf { it > 0 }?.let { emojis[pointer % it] } ?: ""
    }

    private val rotAnimatables = mutableMapOf<Int, Animatable<Float, AnimationVector1D>>()
    fun getRotAnimatable(layer: Int) = rotAnimatables.getOrPut(layer) { Animatable(1f) }

    private val scaleAnimatables = mutableMapOf<Int, Animatable<Float, AnimationVector1D>>()
    fun getScaleAnimatable(layer: Int) = scaleAnimatables.getOrPut(layer) { Animatable(1f) }

    fun updateContainerSize(
        containerSize: IntSize,
    ) {
        this.containerSize = containerSize
    }

    fun update(
        itemDiameterPx: Int,
        textSizePx: Int
    ) {
        this.itemDiameterPx = itemDiameterPx
        this.textSizePx = textSizePx
    }

    fun newEmojis(emojis: List<String>) {
        if (this.emojis == emojis) return

        this.emojis = emojis
        scope.launch {
            wave(
                switchEmojiNowForLayer = { layer ->
                    // Remove the currently shown emoji for the given layer
                    layerToPointers[layer]?.let { pointers ->
                        pointers.forEach { pointer ->
                            // Remove the currently shown emoji for the given pointer.
                            pointerToEmoji.remove(pointer)
                        }
                    }
                }
            )
        }
    }

    fun shake() = scope.launch {
        rotAnimatables.forEach { (layer, animatable) ->
            launch {
                delay(layer * 100L)
                while (true) {
                    animatable.animateTo(-10f, tween(150, easing = LinearEasing))
                    animatable.animateTo(10f, tween(150, easing = LinearEasing))
                }
            }
        }
    }

    fun wave(switchEmojiNowForLayer: (Int) -> Unit) = scope.launch {
        rotAnimatables.forEach { (layer, animatable) ->
            launch {
                delay(layer * 80L)
                animatable.snapTo(30f)
                animatable.animateTo(
                    1f,
                    spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMediumLow)
                )
            }
        }

        scaleAnimatables.forEach { (layer, animatable) ->
            launch {
                delay(layer * 80L)
                animatable.snapTo(1.3f)
                switchEmojiNowForLayer(layer)
                animatable.animateTo(
                    1f,
                    spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMediumLow)
                )
            }
        }
    }

    fun stopAnimations() = scope.launch {
        rotAnimatables.values.forEachIndexed { layer, animatable ->
            launch {
                delay(layer * 80L)
                animatable.animateTo(1f)
            }
        }

        scaleAnimatables.values.forEachIndexed { layer, animatable ->
            launch {
                delay(layer * 80L)
                animatable.animateTo(1f)
            }
        }
    }

    data class Item(
        val emojiPointer: Int,
        val layer: Int,
        val indexInLayer: Int,
        val pos: IntOffset,
        val size: IntSize
    ) {
        val center = Offset(
            x = size.width / 2f,
            y = size.height / 2f
        )
    }
}