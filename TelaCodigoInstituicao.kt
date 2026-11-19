package com.example.helloworld

import android.R.attr.x
import android.R.attr.y
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.helloworld.ui.theme.HelloWorldTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tanh
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import android.graphics.Rect as AndroidRect
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth

val AliceFontFamily = FontFamily(
    Font(R.font.alice_regular, FontWeight.Normal, FontStyle.Normal)
)

data class Instituicao(
    val nome: String,
    val tipo: String,
    val cidade: String,
    val integrantes: Int,
    val verificada: Boolean,
    val logoRes: Int,
    val fotoUrl: String? = null
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HelloWorldTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    CleanLiquidShowcase(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ============================================================================
// TELA PRINCIPAL
// ============================================================================


@SuppressLint("UnusedBoxWithConstraintsScope")
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun CleanLiquidShowcase(modifier: Modifier = Modifier) {
    var codigoInstituicao by remember { mutableStateOf("") }

    val density = LocalDensity.current
    val context = LocalContext.current

    val backgroundBitmap = remember {
        BitmapFactory.decodeResource(
            context.resources,
            R.drawable.forest_background
        )
    }

    BoxWithConstraints(modifier = modifier) {

        val screenW = with(density) {
            maxWidth.roundToPx().coerceAtLeast(1)
        }

        val screenH = with(density) {
            maxHeight.roundToPx().coerceAtLeast(1)
        }

        val engine = remember(screenW, screenH, backgroundBitmap) {
            LiquidBackdropEngine(
                screenWidth = screenW,
                screenHeight = screenH,
                density = density.density,
                sourcePhoto = backgroundBitmap
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawImage(
                image = engine.fullResBackground,
                dstSize = IntSize(
                    size.width.roundToInt(),
                    size.height.roundToInt()
                )
            )
        }

        val larguraCard = (maxWidth - 32.dp)
            .coerceAtMost(370.dp)

        val inicioCards = maxHeight * 0.20f
        val espacamento = 22.dp

        val alturaPrivacidade = (maxHeight * 0.19f)
            .coerceIn(150.dp, 180.dp)

        val alturaPrincipal = (
                maxHeight - inicioCards -
                        espacamento - alturaPrivacidade - 20.dp
                ).coerceAtMost(500.dp).coerceAtLeast(300.dp)

        // CARD PRINCIPAL

        LiquidGlassSurface(
            engine = engine,
            cornerRadius = 28.dp,
            refractionHeight = 22.dp,
            refractionAmount = 22.dp,
            edgeFill = 0.20f,
            isInteractive = false,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = inicioCards)
                .size(
                    width = larguraCard,
                    height = alturaPrincipal
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                @Composable
                fun EtapaInstituicao(
                    numero: String,
                    titulo: String,
                    ativa: Boolean,
                    modifier: Modifier = Modifier
                ) {
                    Row(
                        modifier = modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color.White.copy(alpha = 0.38f))
                            .padding(horizontal = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(23.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (ativa) Color(0xFF435F3F)
                                        else Color(0xFF92917D)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = numero,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = titulo,
                            fontFamily = AliceFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF344530),
                            maxLines = 1
                        )
                    }
                }


                Spacer(modifier = Modifier.height(28.dp))

                LiquidGlassIconButton(
                    engine = engine,
                    iconRes = R.drawable.chave,
                    iconDescription = "Chave da instituição",
                    size = 68.dp,
                    greenCircleSize = 52.dp,
                    iconSize = 30.dp,
                    glassOpacity = 1.6f,
                    centerFill = 0.12f,
                    edgeFill = 0.15f,
                    isInteractive = false
                )

                Spacer(modifier = Modifier.height(22.dp))


                Text(
                    text = "Encontre sua instituição",
                    fontSize = 24.sp,
                    fontFamily = AliceFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF344530),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Digite o código que você recebeu da sua escola, curso ou comunidade.",
                    fontSize = 17.sp,
                    lineHeight = 21.sp,
                    fontFamily = AliceFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF606C58),
                    textAlign = TextAlign.Center
                )


                Spacer(modifier = Modifier.height(12.dp))

                LiquidGlassSurface(
                    engine = engine,
                    cornerRadius = 50.dp,
                    refractionHeight = 14.dp,
                    refractionAmount = 14.dp,
                    edgeFill = 0.15f,
                    isInteractive = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {

                    OutlinedTextField(
                        value = codigoInstituicao,
                        onValueChange = { codigoInstituicao = it },

                        placeholder = {
                            Text(
                                text = "Digite seu código",
                                fontFamily = AliceFontFamily,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF839087)
                            )
                        },

                        leadingIcon = {
                            LiquidGlassIconButton(
                                engine = engine,
                                iconRes = R.drawable.link,
                                iconDescription = "Código",
                                size = 36.dp,
                                greenCircleSize = 27.dp,
                                iconSize = 18.dp,
                                glassOpacity = 1.6f,
                                centerFill = 0.12f,
                                edgeFill = 0.15f,
                                isInteractive = false
                            )
                        },

                        singleLine = true,

                        modifier = Modifier.fillMaxSize(),

                        shape = RoundedCornerShape(50.dp),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color(0xFF344530),
                            unfocusedTextColor = Color(0xFF344530),
                            cursorColor = Color(0xFF435F3F)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Código da instituição",
                    fontSize = 19.sp,
                    fontFamily = AliceFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF344530),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LiquidGlassIconButton(
                        engine = engine,
                        iconRes = R.drawable.informacao_icone,
                        iconDescription = "Informação",
                        size = 34.dp,
                        greenCircleSize = 26.dp,
                        iconSize = 17.dp,
                        glassOpacity = 1.6f,
                        centerFill = 0.12f,
                        edgeFill = 0.15f,
                        isInteractive = false
                    )

                    Text(
                        text = "Não compartilhe esse código com pessoas de fora da instituição.",
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontFamily = AliceFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF606C58),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF435F3F))
                        .clickable {
                            if (codigoInstituicao.isNotBlank()) {
                                // Futuramente: procurar instituição
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Procurar instituição",
                        fontSize = 19.sp,
                        fontFamily = AliceFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Image(
                        painter = painterResource(
                            id = R.drawable.arrow_right
                        ),
                        contentDescription = "Avançar",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 14.dp)
                            .size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

            }
        }

        // CARD DE PRIVACIDADE

        LiquidGlassSurface(
            engine = engine,
            cornerRadius = 28.dp,
            refractionHeight = 22.dp,
            refractionAmount = 22.dp,
            edgeFill = 0.20f,
            isInteractive = false,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(
                    y = inicioCards +
                            alturaPrincipal +
                            espacamento
                )
                .size(
                    width = larguraCard,
                    height = alturaPrivacidade
                )
        ) {


            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 18.dp
                    ),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                LiquidGlassIconButton(
                    engine = engine,
                    iconRes = R.drawable.protecao,
                    iconDescription = "Proteção",
                    size = 52.dp,
                    greenCircleSize = 40.dp,
                    iconSize = 25.dp,
                    glassOpacity = 1.6f,
                    centerFill = 0.12f,
                    edgeFill = 0.15f,
                    isInteractive = false
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Text(
                        text = "Sua privacidade continua sendo sua",
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontFamily = AliceFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF344530)
                    )

                    Text(
                        text = "Ao entrar uma instituição, apenas informações gerais da comunidade são compartilhadas.",
                        fontSize = 16.sp,
                        lineHeight = 21.sp,
                        fontFamily = AliceFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF606C58)
                    )
                }
            }

        }
    }
}


@Composable
fun EtapaInstituicao(
    numero: String,
    titulo: String,
    ativa: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White.copy(alpha = 0.38f))
            .padding(horizontal = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(23.dp)
                    .clip(CircleShape)
                    .background(
                        if (ativa) Color(0xFF435F3F)
                        else Color(0xFF92917D)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = numero,
                    fontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = titulo,
            fontFamily = AliceFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF344530),
            maxLines = 1
        )
    }
}


// ============================================================================
// BOTÃO REUTILIZÁVEL: LIQUID GLASS + CÍRCULO VERDE + ÍCONE
// ============================================================================

@Composable
fun LiquidGlassIconButton(
    engine: LiquidBackdropEngine,
    iconRes: Int,
    iconDescription: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    greenCircleSize: Dp = 23.dp,
    iconSize: Dp = 15.dp,
    greenColor: Color = Color(0xFF435F3F),
    glassOpacity: Float = 1.6f,
    centerFill: Float = 0.12f,
    edgeFill: Float = 0.15f,
    isInteractive: Boolean = true
) {
    // Guarda a posição do componente para o Liquid Glass enxergar
    // a parte correta da imagem de fundo.
    var iconPosition by remember {
        mutableStateOf(Offset.Zero)
    }

    Box(
        modifier = modifier
            .size(size)
            .onGloballyPositioned { coordinates ->
                iconPosition = coordinates.positionInRoot()
            },
        contentAlignment = Alignment.Center
    ) {
        // Camada externa de Liquid Glass.
        LiquidGlassSurface(
            engine = engine,
            modifier = Modifier.fillMaxSize(),
            cornerRadius = size / 2,
            refractionHeight = 12.dp,
            refractionAmount = 12.dp,
            glassOpacity = glassOpacity,
            centerFill = centerFill,
            edgeFill = edgeFill,
            showRim = false,
            showShadow = false,
            isInteractive = isInteractive,
            onClick = onClick,
            positionOverride = iconPosition
        ) {}

        // Círculo verde central.
        Box(
            modifier = Modifier
                .size(greenCircleSize)
                .clip(CircleShape)
                .background(greenColor),
            contentAlignment = Alignment.Center
        ) {
            // Ícone centralizado.
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = iconDescription,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

// ============================================================================
// COMPONENTE AUXILIAR DOS BOTÕES
// ============================================================================

@Composable
fun LiquidActionButton(
    engine: LiquidBackdropEngine,
    text: String,
    iconRes: Int,
    iconDescription: String,
    modifier: Modifier = Modifier,
    textOffsetX: Dp = 0.dp,
    onClick: () -> Unit = {},
    glassOpacity: Float = 1f
) {
    LiquidGlassSurface(
        engine = engine,
        glassOpacity = glassOpacity,
        centerFill = 0.25f,
        cornerRadius = 16.dp,
        refractionHeight = 12.dp,
        refractionAmount = 12.dp,
        isInteractive = true,
        onClick = onClick,
        showRim = false,
        showShadow = false,
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Componente reutilizável: vidro + círculo verde + ícone.
            LiquidGlassIconButton(
                engine = engine,
                iconRes = iconRes,
                iconDescription = iconDescription,
                onClick = onClick,
                size = 32.dp,
                greenCircleSize = 23.dp,
                iconSize = 15.dp,
                glassOpacity = 1.6f,
                centerFill = 0.12f,
                edgeFill = 0.15f,
                isInteractive = false,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 5.5.dp)
                    .zIndex(1f)
            )

            // Texto.
            Text(
                text = text,
                lineHeight = 15.sp,
                color = Color(0xFF2C3E35),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = AliceFontFamily,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = textOffsetX)
                    .zIndex(3f)
            )

            // Seta.
            Image(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = "Seta",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(20.dp)
                    .offset(x = (-3).dp)
                    .zIndex(3f)
            )
        }
    }
}

// ============================================================================
// COMPONENTE PRINCIPAL LIQUID GLASS
// ============================================================================
@Composable
fun CardInstituicao(
    engine: LiquidBackdropEngine,
    instituicao: Instituicao,
    modifier: Modifier = Modifier
) {
    val integrantesFormatado = NumberFormat
        .getInstance(Locale.forLanguageTag("pt-BR"))
        .format(instituicao.integrantes)

    LiquidGlassSurface(
        engine = engine,
        cornerRadius = 24.dp,
        refractionHeight = 22.dp,
        refractionAmount = 22.dp,
        edgeFill = 0.20f,
        isInteractive = false,
        modifier = modifier.size(width = 310.dp, height = 88.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = instituicao.logoRes),
                    contentDescription = instituicao.nome,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = instituicao.nome,
                        fontSize = 17.sp,
                        fontFamily = AliceFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF344530),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (instituicao.verificada) {
                        Image(
                            painter = painterResource(id = R.drawable.verificado),
                            contentDescription = "Verificada",
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = "${instituicao.tipo} • ${instituicao.cidade}",
                    fontSize = 12.sp,
                    fontFamily = AliceFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF344530).copy(alpha = 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.integrantes),
                        contentDescription = "Integrantes",
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = integrantesFormatado,
                        fontSize = 12.sp,
                        fontFamily = AliceFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF344530)
                    )

                    Text(
                        text = "Integrantes",
                        fontSize = 12.sp,
                        fontFamily = AliceFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF344530).copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}


@Composable
fun LiquidGlassSurface(
    engine: LiquidBackdropEngine,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    refractionHeight: Dp = 22.dp,
    refractionAmount: Dp = 22.dp,
    isInteractive: Boolean = true,
    onClick: (() -> Unit)? = null,
    positionOverride: Offset? = null,
    showRim: Boolean = true,
    showShadow: Boolean = true,
    glassOpacity: Float = 1f,
    centerFill: Float = 0f,
    edgeFill: Float = 0f,
    content: @Composable BoxScope.() -> Unit
) {

    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var cardPos by remember { mutableStateOf(Offset.Zero) }
    var cardSize by remember { mutableStateOf(IntSize.Zero) }

    val pressProgress = remember { Animatable(0f) }

    var touchOffset by remember { mutableStateOf(Offset.Zero) }

    var lastTouchLocal by remember {
        mutableStateOf(Offset.Unspecified)
    }

    val cornerPx = with(density) {
        cornerRadius.toPx()
    }

    val refHeightPx = with(density) {
        refractionHeight.toPx()
    }

    val refAmountPx = with(density) {
        refractionAmount.toPx()
    }

    val shadowPadPx = with(density) {
        32.dp.roundToPx()
    }

    val glassAssets = remember(
        cardSize,
        cornerPx,
        refHeightPx,
        refAmountPx,
        showRim,
        showShadow,
        glassOpacity,
        density,
        centerFill,
        edgeFill
    ) {

        if (cardSize.width > 0 && cardSize.height > 0) {

            GlassCardAssets.build(
                cardW = cardSize.width,
                cardH = cardSize.height,
                cornerRadiusPx = cornerPx,
                refractionHeightPx = refHeightPx,
                refractionAmountPx = refAmountPx,
                showRim = showRim,
                glassOpacity = glassOpacity,
                centerFill = centerFill,
                edgeFill = edgeFill,
                shadowPadPx = shadowPadPx,
                density = density.density,
                bufferScale = engine.scaleFactor
            )

        } else null
    }

    val dstRect = remember {
        AndroidRect()
    }

    val bitmapPaint = remember {
        AndroidPaint(
            AndroidPaint.ANTI_ALIAS_FLAG or
                    AndroidPaint.FILTER_BITMAP_FLAG
        )
    }

    Box(
        modifier = modifier

            .onGloballyPositioned { coords ->
                cardPos = positionOverride ?: coords.positionInParent()
                cardSize = coords.size
            }

            .then(
                if (isInteractive) {

                    Modifier.pointerInput(onClick) {

                        awaitEachGesture {

                            val down = awaitFirstDown(
                                requireUnconsumed = false
                            )

                            val startPos = down.position

                            lastTouchLocal = startPos

                            scope.launch {
                                pressProgress.animateTo(
                                    1f,
                                    spring(
                                        dampingRatio = 0.65f,
                                        stiffness = 380f
                                    )
                                )
                            }

                            var dragged = false

                            while (true) {

                                val event = awaitPointerEvent()

                                val change = event.changes.firstOrNull()
                                    ?: break

                                if (!change.pressed) {

                                    if (!dragged) {
                                        onClick?.invoke()
                                    }

                                    break
                                }

                                val delta = change.position - startPos

                                if (delta.getDistance() > 8f) {
                                    dragged = true
                                }

                                touchOffset = delta
                                lastTouchLocal = change.position
                            }

                            touchOffset = Offset.Zero

                            scope.launch {
                                pressProgress.animateTo(
                                    0f,
                                    spring(
                                        dampingRatio = 0.6f,
                                        stiffness = 300f
                                    )
                                )
                            }
                        }
                    }

                } else Modifier
            )

            .graphicsLayer {

                if (isInteractive && size.width > 0f && size.height > 0f) {

                    val p = pressProgress.value

                    val baseScale = 1f +
                            (4.dp.toPx() / size.height) * p

                    val maxOffset = size.minDimension

                    val k = 0.04f

                    translationX = maxOffset *
                            tanh(k * touchOffset.x / maxOffset)

                    translationY = maxOffset *
                            tanh(k * touchOffset.y / maxOffset)

                    val maxDragScale = 4.dp.toPx() / size.height

                    val angle = atan2(
                        touchOffset.y,
                        touchOffset.x
                    )

                    val distRatio = (
                            touchOffset.getDistance() /
                                    size.maxDimension
                            ).coerceAtMost(1f)

                    scaleX = baseScale +
                            maxDragScale * abs(cos(angle)) * distRatio

                    scaleY = baseScale +
                            maxDragScale * abs(sin(angle)) * distRatio
                }
            }

    ) {

        if (showShadow) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val assets = glassAssets ?: return@Canvas

                drawImage(
                    image = assets.outerShadowBitmap,
                    topLeft = Offset(
                        -shadowPadPx.toFloat(),
                        -shadowPadPx.toFloat()
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
        ) {

            Canvas(modifier = Modifier.fillMaxSize()) {

                val assets = glassAssets ?: return@Canvas

                val refractedBmp = assets.updateRefractedBitmap(
                    engine = engine,
                    cardX = cardPos.x,
                    cardY = cardPos.y
                )

                drawIntoCanvas { canvas ->

                    dstRect.set(
                        0,
                        0,
                        size.width.roundToInt(),
                        size.height.roundToInt()
                    )

                    canvas.nativeCanvas.drawBitmap(
                        refractedBmp,
                        null,
                        dstRect,
                        bitmapPaint
                    )
                }

                if (
                    pressProgress.value > 0.01f &&
                    lastTouchLocal != Offset.Unspecified
                ) {

                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(
                                    alpha = 0.18f * pressProgress.value
                                ),
                                Color.Transparent
                            ),
                            center = lastTouchLocal,
                            radius = size.maxDimension * 0.6f
                        )
                    )
                }

                drawImage(
                    image = assets.innerGlassOverlayBitmap,
                    dstSize = IntSize(
                        size.width.roundToInt(),
                        size.height.roundToInt()
                    )
                )
            }

            content()
        }
    }
}

// ============================================================================
// MOTOR DE FUNDO FOTOGRÁFICO
// ============================================================================

class LiquidBackdropEngine(
    val screenWidth: Int,
    val screenHeight: Int,
    val density: Float,
    sourcePhoto: Bitmap? = null
) {

    val scaleFactor = 3

    val bufW = (screenWidth / scaleFactor).coerceAtLeast(64)
    val bufH = (screenHeight / scaleFactor).coerceAtLeast(64)

    val fullResBackground: ImageBitmap

    val sharpVibrantPixels = IntArray(bufW * bufH)
    val blurredVibrantPixels = IntArray(bufW * bufH)

    init {

        val fullBmp = Bitmap.createBitmap(
            screenWidth,
            screenHeight,
            Bitmap.Config.ARGB_8888
        )

        val fullCanvas = AndroidCanvas(fullBmp)

        if (sourcePhoto != null) {

            drawBitmapCenterCrop(
                fullCanvas,
                sourcePhoto,
                screenWidth.toFloat(),
                screenHeight.toFloat()
            )

        } else {

            drawRealisticLandscapeFallback(
                fullCanvas,
                screenWidth.toFloat(),
                screenHeight.toFloat(),
                density
            )
        }

        fullResBackground = fullBmp.asImageBitmap()

        val smallBmp = Bitmap.createScaledBitmap(
            fullBmp,
            bufW,
            bufH,
            true
        )

        smallBmp.getPixels(
            sharpVibrantPixels,
            0,
            bufW,
            0,
            0,
            bufW,
            bufH
        )

        applyVibrancyInPlace(
            sharpVibrantPixels,
            saturation = 1.55f,
            brightness = 1.06f
        )

        System.arraycopy(
            sharpVibrantPixels,
            0,
            blurredVibrantPixels,
            0,
            sharpVibrantPixels.size
        )

        val edgeBlurRadius = (
                (2f * density) / scaleFactor
                ).roundToInt().coerceAtLeast(1)

        gaussianBlur3Pass(
            sharpVibrantPixels,
            bufW,
            bufH,
            edgeBlurRadius
        )

        val bodyBlurRadius = (
                (16f * density) / scaleFactor
                ).roundToInt().coerceAtLeast(3)

        gaussianBlur3Pass(
            blurredVibrantPixels,
            bufW,
            bufH,
            bodyBlurRadius
        )
    }

    private fun drawBitmapCenterCrop(
        canvas: AndroidCanvas,
        src: Bitmap,
        dstW: Float,
        dstH: Float
    ) {

        val srcW = src.width.toFloat()
        val srcH = src.height.toFloat()

        val scale = max(
            dstW / srcW,
            dstH / srcH
        )

        val dx = (dstW - srcW * scale) * 0.5f
        val dy = (dstH - srcH * scale) * 0.5f

        val matrix = Matrix().apply {
            setScale(scale, scale)
            postTranslate(dx, dy)
        }

        val paint = AndroidPaint(
            AndroidPaint.ANTI_ALIAS_FLAG or
                    AndroidPaint.FILTER_BITMAP_FLAG
        )

        canvas.drawBitmap(src, matrix, paint)
    }

    private fun drawRealisticLandscapeFallback(
        canvas: AndroidCanvas,
        w: Float,
        h: Float,
        d: Float
    ) {

        val paint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG)

        paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            h * 0.65f,
            intArrayOf(
                0xFF4A7C9B.toInt(),
                0xFF8AB8C9.toInt(),
                0xFFF3D5B5.toInt(),
                0xFFF7B28B.toInt()
            ),
            floatArrayOf(0f, 0.45f, 0.82f, 1f),
            Shader.TileMode.CLAMP
        )

        canvas.drawRect(0f, 0f, w, h, paint)

        val sunX = w * 0.72f
        val sunY = h * 0.36f
        val sunR = 110f * d

        paint.shader = RadialGradient(
            sunX,
            sunY,
            sunR,
            intArrayOf(
                0xFFFFF6E0.toInt(),
                0x99FFD19A.toInt(),
                0x00FFD19A
            ),
            floatArrayOf(0f, 0.45f, 1f),
            Shader.TileMode.CLAMP
        )

        canvas.drawCircle(
            sunX,
            sunY,
            sunR,
            paint
        )

        val mtn1 = AndroidPath().apply {

            moveTo(0f, h * 0.58f)
            lineTo(w * 0.22f, h * 0.43f)
            lineTo(w * 0.46f, h * 0.51f)
            lineTo(w * 0.76f, h * 0.39f)
            lineTo(w, h * 0.48f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        paint.shader = LinearGradient(
            0f,
            h * 0.39f,
            0f,
            h * 0.65f,
            intArrayOf(
                0xFF3E6373.toInt(),
                0xFF6B8F99.toInt()
            ),
            null,
            Shader.TileMode.CLAMP
        )

        canvas.drawPath(mtn1, paint)

        val mtn2 = AndroidPath().apply {

            moveTo(0f, h * 0.54f)
            lineTo(w * 0.34f, h * 0.46f)
            lineTo(w * 0.66f, h * 0.55f)
            lineTo(w, h * 0.47f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        paint.shader = LinearGradient(
            0f,
            h * 0.46f,
            0f,
            h * 0.72f,
            intArrayOf(
                0xFF1F4E46.toInt(),
                0xFF112E2A.toInt()
            ),
            null,
            Shader.TileMode.CLAMP
        )

        canvas.drawPath(mtn2, paint)

        paint.shader = LinearGradient(
            0f,
            h * 0.62f,
            0f,
            h,
            intArrayOf(
                0xFF287672.toInt(),
                0xFF195456.toInt(),
                0xFF0D2E33.toInt()
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )

        canvas.drawRect(
            0f,
            h * 0.62f,
            w,
            h,
            paint
        )
    }
}

// ============================================================================
// GERADOR ÓPTICO DO LIQUID GLASS
// ============================================================================

class GlassCardAssets(
    val lensW: Int,
    val lensH: Int,
    val bufferScale: Int,
    val offsetX_R: FloatArray,
    val offsetY_R: FloatArray,
    val offsetX_G: FloatArray,
    val offsetY_G: FloatArray,
    val offsetX_B: FloatArray,
    val offsetY_B: FloatArray,
    val rimEdgeFactor: IntArray,
    val causticBoost: IntArray,
    val outerShadowBitmap: ImageBitmap,
    val innerGlassOverlayBitmap: ImageBitmap
) {

    private val outPixels = IntArray(lensW * lensH)

    private val outBitmap = Bitmap.createBitmap(
        lensW,
        lensH,
        Bitmap.Config.ARGB_8888
    )

    fun updateRefractedBitmap(
        engine: LiquidBackdropEngine,
        cardX: Float,
        cardY: Float
    ): Bitmap {

        val bgW = engine.bufW
        val bgH = engine.bufH

        val blurred = engine.blurredVibrantPixels
        val sharp = engine.sharpVibrantPixels

        val baseBx = cardX / bufferScale
        val baseBy = cardY / bufferScale

        val total = lensW * lensH

        for (i in 0 until total) {

            val lx = i % lensW
            val ly = i / lensW

            val bx = baseBx + lx
            val by = baseBy + ly

            val rx = (
                    bx + offsetX_R[i]
                    ).toInt().coerceIn(0, bgW - 1)

            val ry = (
                    by + offsetY_R[i]
                    ).toInt().coerceIn(0, bgH - 1)

            val gx = (
                    bx + offsetX_G[i]
                    ).toInt().coerceIn(0, bgW - 1)

            val gy = (
                    by + offsetY_G[i]
                    ).toInt().coerceIn(0, bgH - 1)

            val blx = (
                    bx + offsetX_B[i]
                    ).toInt().coerceIn(0, bgW - 1)

            val bly = (
                    by + offsetY_B[i]
                    ).toInt().coerceIn(0, bgH - 1)

            val idxR = ry * bgW + rx
            val idxG = gy * bgW + gx
            val idxB = bly * bgW + blx

            val rimMix = rimEdgeFactor[i]
            val invMix = 255 - rimMix

            val r = (
                    (
                            (blurred[idxR] shr 16 and 0xFF) * invMix +
                                    (sharp[idxR] shr 16 and 0xFF) * rimMix
                            ) shr 8
                    )

            val g = (
                    (
                            (blurred[idxG] shr 8 and 0xFF) * invMix +
                                    (sharp[idxG] shr 8 and 0xFF) * rimMix
                            ) shr 8
                    )

            val b = (
                    (
                            (blurred[idxB] and 0xFF) * invMix +
                                    (sharp[idxB] and 0xFF) * rimMix
                            ) shr 8
                    )

            val boost = causticBoost[i]

            val finalR = (r + boost).coerceAtMost(255)
            val finalG = (g + boost).coerceAtMost(255)
            val finalB = (b + boost).coerceAtMost(255)

            outPixels[i] =
                (0xFF shl 24) or
                        (finalR shl 16) or
                        (finalG shl 8) or
                        finalB
        }

        outBitmap.setPixels(
            outPixels,
            0,
            lensW,
            0,
            0,
            lensW,
            lensH
        )

        return outBitmap
    }

    companion object {

        fun build(
            cardW: Int,
            cardH: Int,
            cornerRadiusPx: Float,
            refractionHeightPx: Float,
            refractionAmountPx: Float,
            showRim: Boolean,
            glassOpacity: Float,
            centerFill: Float,
            edgeFill: Float,
            shadowPadPx: Int,
            density: Float,
            bufferScale: Int
        ): GlassCardAssets {

            val lensW = (
                    cardW / bufferScale
                    ).coerceAtLeast(16)

            val lensH = (
                    cardH / bufferScale
                    ).coerceAtLeast(16)

            val total = lensW * lensH

            val offsetX_R = FloatArray(total)
            val offsetY_R = FloatArray(total)

            val offsetX_G = FloatArray(total)
            val offsetY_G = FloatArray(total)

            val offsetX_B = FloatArray(total)
            val offsetY_B = FloatArray(total)

            val rimEdgeFactor = IntArray(total)
            val causticBoost = IntArray(total)

            val halfW = lensW / 2f
            val halfH = lensH / 2f

            val rScaled = (
                    cornerRadiusPx / bufferScale
                    ).coerceAtMost(min(halfW, halfH))

            val refHeightScaled = (
                    refractionHeightPx / bufferScale
                    ).coerceAtLeast(1f)

            val refAmountScaled = refractionAmountPx / bufferScale

            fun sdfRoundedRect(
                px: Float,
                py: Float
            ): Float {

                val qx = abs(px - halfW) - (halfW - rScaled)
                val qy = abs(py - halfH) - (halfH - rScaled)

                val outDist = sqrt(
                    max(qx, 0f) * max(qx, 0f) +
                            max(qy, 0f) * max(qy, 0f)
                )

                val inDist = min(
                    max(qx, qy),
                    0f
                )

                return outDist + inDist - rScaled
            }

            for (y in 0 until lensH) {

                for (x in 0 until lensW) {

                    val i = y * lensW + x

                    val px = x + 0.5f
                    val py = y + 0.5f

                    val sdf = sdfRoundedRect(px, py)

                    val distFromBorder = -sdf

                    if (distFromBorder in 0f..refHeightScaled) {

                        val gx =
                            sdfRoundedRect(px + 1f, py) -
                                    sdfRoundedRect(px - 1f, py)

                        val gy =
                            sdfRoundedRect(px, py + 1f) -
                                    sdfRoundedRect(px, py - 1f)

                        val len = sqrt(
                            gx * gx + gy * gy
                        ).coerceAtLeast(0.0001f)

                        val nx = gx / len
                        val ny = gy / len

                        val u = (
                                1f - (distFromBorder / refHeightScaled)
                                ).coerceIn(0f, 1f)

                        val lensCurve =
                            u * u * (2.05f - 1.05f * u)

                        val disp = -refAmountScaled * lensCurve

                        offsetX_R[i] = nx * disp * 1.04f
                        offsetY_R[i] = ny * disp * 1.04f

                        offsetX_G[i] = nx * disp * 1.00f
                        offsetY_G[i] = ny * disp * 1.00f

                        offsetX_B[i] = nx * disp * 0.96f
                        offsetY_B[i] = ny * disp * 0.96f

                        if (showRim) {

                            rimEdgeFactor[i] = (
                                    u * u * 115f
                                    ).toInt().coerceIn(0, 115)

                            val ring = sin(
                                u * Math.PI.toFloat()
                            )

                            causticBoost[i] = (
                                    ring * ring * 7f
                                    ).toInt()

                        } else {

                            rimEdgeFactor[i] = 0
                            causticBoost[i] = 0
                        }
                    }
                }
            }

            val outerShadowBmp = buildOuterShadowBitmap(
                cardW,
                cardH,
                cornerRadiusPx,
                shadowPadPx,
                density
            )

            val innerOverlayBmp = buildInnerGlassOverlayBitmap(
                cardW,
                cardH,
                cornerRadiusPx,
                density,
                glassOpacity,
                centerFill,
                edgeFill
            )

            return GlassCardAssets(
                lensW = lensW,
                lensH = lensH,
                bufferScale = bufferScale,
                offsetX_R = offsetX_R,
                offsetY_R = offsetY_R,
                offsetX_G = offsetX_G,
                offsetY_G = offsetY_G,
                offsetX_B = offsetX_B,
                offsetY_B = offsetY_B,
                rimEdgeFactor = rimEdgeFactor,
                causticBoost = causticBoost,
                outerShadowBitmap = outerShadowBmp.asImageBitmap(),
                innerGlassOverlayBitmap = innerOverlayBmp.asImageBitmap()
            )
        }

        // ================================================================
        // SOMBRA EXTERNA
        // ================================================================

        private fun buildOuterShadowBitmap(
            cardW: Int,
            cardH: Int,
            cornerRadiusPx: Float,
            padPx: Int,
            density: Float
        ): Bitmap {

            val fullW = cardW + padPx * 2
            val fullH = cardH + padPx * 2

            val scale = 2

            val sw = (fullW / scale).coerceAtLeast(8)
            val sh = (fullH / scale).coerceAtLeast(8)

            val shadowBmp = Bitmap.createBitmap(
                sw,
                sh,
                Bitmap.Config.ARGB_8888
            )

            val canvas = AndroidCanvas(shadowBmp)

            val paint = AndroidPaint(
                AndroidPaint.ANTI_ALIAS_FLAG
            )

            val spread1 = -6f * density / scale
            val offY1 = 12f * density / scale

            val rect1 = RectF(
                (padPx / scale) - spread1,
                (padPx / scale) - spread1 + offY1,
                ((padPx + cardW) / scale) + spread1,
                ((padPx + cardH) / scale) + spread1 + offY1
            )

            paint.color = 0x220F2012

            val r1 = (
                    cornerRadiusPx / scale + spread1
                    ).coerceAtLeast(0f)

            canvas.drawRoundRect(
                rect1,
                r1,
                r1,
                paint
            )

            val pixels1 = IntArray(sw * sh)

            shadowBmp.getPixels(
                pixels1,
                0,
                sw,
                0,
                0,
                sw,
                sh
            )

            gaussianBlur3Pass(
                pixels1,
                sw,
                sh,
                (
                        (12f * density) / scale
                        ).roundToInt().coerceAtLeast(2)
            )

            shadowBmp.eraseColor(0)

            val spread2 = -2f * density / scale
            val offY2 = 3f * density / scale

            val rect2 = RectF(
                (padPx / scale) - spread2,
                (padPx / scale) - spread2 + offY2,
                ((padPx + cardW) / scale) + spread2,
                ((padPx + cardH) / scale) + spread2 + offY2
            )

            paint.color = 0x10000000

            val r2 = (
                    cornerRadiusPx / scale + spread2
                    ).coerceAtLeast(0f)

            canvas.drawRoundRect(
                rect2,
                r2,
                r2,
                paint
            )

            val pixels2 = IntArray(sw * sh)

            shadowBmp.getPixels(
                pixels2,
                0,
                sw,
                0,
                0,
                sw,
                sh
            )

            gaussianBlur3Pass(
                pixels2,
                sw,
                sh,
                (
                        (4f * density) / scale
                        ).roundToInt().coerceAtLeast(1)
            )

            val finalSmallBmp = Bitmap.createBitmap(
                sw,
                sh,
                Bitmap.Config.ARGB_8888
            )

            finalSmallBmp.setPixels(
                pixels1,
                0,
                sw,
                0,
                0,
                sw,
                sh
            )

            val combineCanvas = AndroidCanvas(finalSmallBmp)

            shadowBmp.setPixels(
                pixels2,
                0,
                sw,
                0,
                0,
                sw,
                sh
            )

            combineCanvas.drawBitmap(
                shadowBmp,
                0f,
                0f,
                null
            )

            val fullShadowBmp = Bitmap.createScaledBitmap(
                finalSmallBmp,
                fullW,
                fullH,
                true
            )

            val fullCanvas = AndroidCanvas(fullShadowBmp)

            val clearPaint = AndroidPaint(
                AndroidPaint.ANTI_ALIAS_FLAG
            ).apply {
                xfermode = PorterDuffXfermode(
                    PorterDuff.Mode.CLEAR
                )
            }

            fullCanvas.drawRoundRect(
                RectF(
                    padPx.toFloat(),
                    padPx.toFloat(),
                    (padPx + cardW).toFloat(),
                    (padPx + cardH).toFloat()
                ),
                cornerRadiusPx,
                cornerRadiusPx,
                clearPaint
            )

            return fullShadowBmp
        }

        // ================================================================
        // CAMADA INTERNA DO VIDRO
        // ================================================================

        private fun buildInnerGlassOverlayBitmap(
            cardW: Int,
            cardH: Int,
            cornerRadiusPx: Float,
            density: Float,
            glassOpacity: Float,
            centerFill: Float,
            edgeFill: Float

        ): Bitmap {

            val resultBmp = Bitmap.createBitmap(
                cardW,
                cardH,
                Bitmap.Config.ARGB_8888
            )

            val canvas = AndroidCanvas(resultBmp)

            val w = cardW.toFloat()
            val h = cardH.toFloat()

            val borderPx = 0f

            val innerR = cornerRadiusPx

            val paddingBoxPath = AndroidPath().apply {

                addRoundRect(
                    RectF(
                        borderPx,
                        borderPx,
                        w - borderPx,
                        h - borderPx
                    ),
                    innerR,
                    innerR,
                    AndroidPath.Direction.CW
                )
            }

            canvas.save()
            canvas.clipPath(paddingBoxPath)

            val paint = AndroidPaint(
                AndroidPaint.ANTI_ALIAS_FLAG
            )

            fun adjustAlpha(color: Int): Int {
                val alpha = (((color ushr 24) * glassOpacity).toInt()).coerceIn(0, 255)
                return (color and 0x00FFFFFF) or (alpha shl 24)
            }

            // OPACIDADE NAS BORDAS
            if (edgeFill > 0f) {
                val ringW = 8f * density
                val ringPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
                    style = AndroidPaint.Style.STROKE
                    strokeWidth = ringW * 2f
                    color = ((255 * edgeFill).toInt().coerceIn(0, 255) shl 24) or 0x00FFFFFF
                    maskFilter = BlurMaskFilter(ringW * 0.6f, BlurMaskFilter.Blur.NORMAL)
                }
                canvas.drawRoundRect(RectF(0f, 0f, w, h), innerR, innerR, ringPaint)
            }

            // FUNDO GRADIENTE DO VIDRO
            paint.shader = LinearGradient(
                0f,
                0f,
                w,
                h,
                intArrayOf(
                    adjustAlpha(0x55FFFFFF),
                    adjustAlpha(0x55FFFFFF),
                    adjustAlpha(0x55FFFFFF)
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )

            canvas.drawRect(
                0f,
                0f,
                w,
                h,
                paint
            )

            // BRILHO SUPERIOR ESQUERDO
            val cx = w * 0.30f
            val cy = 0f

            val rx = (
                    w * 1.30f
                    ).coerceAtLeast(1f)

            val ry = (
                    h * 0.85f
                    ).coerceAtLeast(1f)

            val radialShader = RadialGradient(
                cx,
                cy,
                rx,
                intArrayOf(
                    adjustAlpha(0x79FFFFFF),
                    adjustAlpha(0x45FFFFFF),
                    adjustAlpha(0x79FFFFFF)
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )

            val ellipseMatrix = Matrix().apply {
                setScale(
                    1f,
                    ry / rx,
                    cx,
                    cy
                )
            }

            radialShader.setLocalMatrix(ellipseMatrix)

            paint.shader = radialShader

            canvas.drawRect(
                0f,
                0f,
                w,
                h,
                paint
            )

            paint.shader = null

            // PREENCHIMENTO DO CENTRO
            if (centerFill > 0f) {
                val inset = min(w, h) * 0.22f
                val fillPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
                    color = ((255 * centerFill).toInt().coerceIn(0, 255) shl 24) or 0x00FFFFFF
                    maskFilter = BlurMaskFilter(inset * 0.8f, BlurMaskFilter.Blur.NORMAL)
                }
                canvas.drawRoundRect(RectF(inset, inset, w - inset, h - inset), innerR, innerR, fillPaint)
            }

            return resultBmp
        }
    }
}

// ============================================================================
// PROCESSAMENTO DE IMAGEM: VIBRANCY E GAUSSIAN BLUR
// ============================================================================

private fun applyVibrancyInPlace(
    pixels: IntArray,
    saturation: Float,
    brightness: Float
) {

    for (i in pixels.indices) {

        val c = pixels[i]

        val a = c ushr 24

        var r = (c shr 16 and 0xFF).toFloat()
        var g = (c shr 8 and 0xFF).toFloat()
        var b = (c and 0xFF).toFloat()

        val lum =
            0.2126f * r +
                    0.7152f * g +
                    0.0722f * b

        r = (lum + (r - lum) * saturation) * brightness
        g = (lum + (g - lum) * saturation) * brightness
        b = (lum + (b - lum) * saturation) * brightness

        val ir = r.toInt().coerceIn(0, 255)
        val ig = g.toInt().coerceIn(0, 255)
        val ib = b.toInt().coerceIn(0, 255)

        pixels[i] =
            (a shl 24) or
                    (ir shl 16) or
                    (ig shl 8) or
                    ib
    }
}

// ============================================================================
// GAUSSIAN BLUR
// ============================================================================

private fun gaussianBlur3Pass(
    pixels: IntArray,
    w: Int,
    h: Int,
    radius: Int
) {

    if (radius < 1) return

    val temp = IntArray(w * h)

    repeat(3) {

        boxBlurHorizontal(
            pixels,
            temp,
            w,
            h,
            radius
        )

        boxBlurVertical(
            temp,
            pixels,
            w,
            h,
            radius
        )
    }
}

// ============================================================================
// BLUR HORIZONTAL
// ============================================================================

private fun boxBlurHorizontal(
    src: IntArray,
    dst: IntArray,
    w: Int,
    h: Int,
    r: Int
) {

    val div = 2 * r + 1

    for (y in 0 until h) {

        var sumA = 0
        var sumR = 0
        var sumG = 0
        var sumB = 0

        val rowOffset = y * w

        for (i in -r..r) {

            val c = src[
                rowOffset + i.coerceIn(0, w - 1)
            ]

            sumA += (c ushr 24) and 0xFF
            sumR += (c shr 16) and 0xFF
            sumG += (c shr 8) and 0xFF
            sumB += c and 0xFF
        }

        for (x in 0 until w) {

            dst[rowOffset + x] =
                ((sumA / div) shl 24) or
                        ((sumR / div) shl 16) or
                        ((sumG / div) shl 8) or
                        (sumB / div)

            val outIdx = (
                    x - r
                    ).coerceIn(0, w - 1)

            val inIdx = (
                    x + r + 1
                    ).coerceIn(0, w - 1)

            val cOut = src[rowOffset + outIdx]
            val cIn = src[rowOffset + inIdx]

            sumA +=
                ((cIn ushr 24) and 0xFF) -
                        ((cOut ushr 24) and 0xFF)

            sumR +=
                ((cIn shr 16) and 0xFF) -
                        ((cOut shr 16) and 0xFF)

            sumG +=
                ((cIn shr 8) and 0xFF) -
                        ((cOut shr 8) and 0xFF)

            sumB +=
                (cIn and 0xFF) -
                        (cOut and 0xFF)
        }
    }
}

// ============================================================================
// BLUR VERTICAL
// ============================================================================

private fun boxBlurVertical(
    src: IntArray,
    dst: IntArray,
    w: Int,
    h: Int,
    r: Int
) {

    val div = 2 * r + 1

    for (x in 0 until w) {

        var sumA = 0
        var sumR = 0
        var sumG = 0
        var sumB = 0

        for (i in -r..r) {

            val c = src[
                i.coerceIn(0, h - 1) * w + x
            ]

            sumA += (c ushr 24) and 0xFF
            sumR += (c shr 16) and 0xFF
            sumG += (c shr 8) and 0xFF
            sumB += c and 0xFF
        }

        for (y in 0 until h) {

            dst[y * w + x] =
                ((sumA / div) shl 24) or
                        ((sumR / div) shl 16) or
                        ((sumG / div) shl 8) or
                        (sumB / div)

            val outIdx = (
                    y - r
                    ).coerceIn(0, h - 1)

            val inIdx = (
                    y + r + 1
                    ).coerceIn(0, h - 1)

            val cOut = src[outIdx * w + x]
            val cIn = src[inIdx * w + x]

            sumA +=
                ((cIn ushr 24) and 0xFF) -
                        ((cOut ushr 24) and 0xFF)

            sumR +=
                ((cIn shr 16) and 0xFF) -
                        ((cOut shr 16) and 0xFF)

            sumG +=
                ((cIn shr 8) and 0xFF) -
                        ((cOut shr 8) and 0xFF)

            sumB +=
                (cIn and 0xFF) -
                        (cOut and 0xFF)
        }
    }
}