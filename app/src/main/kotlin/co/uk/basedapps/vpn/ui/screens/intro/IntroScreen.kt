package co.uk.basedapps.vpn.ui.screens.intro

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.uk.basedapps.vpn.R
import co.uk.basedapps.vpn.common.ext.openWeb
import co.uk.basedapps.vpn.ui.screens.intro.widget.BulletBlock
import co.uk.basedapps.vpn.ui.screens.intro.widget.PagerIndicator
import co.uk.basedapps.vpn.ui.screens.intro.widget.SentinelLink
import co.uk.basedapps.vpn.ui.widget.BasedButton
import co.uk.basedapps.vpn.ui.widget.ButtonSize
import co.uk.basedapps.vpn.ui.widget.ButtonStyle
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroScreen(
  navigateToDashboard: () -> Unit,
) {
  val pagerState = rememberPagerState(
    pageCount = { 3 },
  )
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF22262E),
            Color(0xFF14161B),
          ),
        ),
      )
      .fillMaxSize()
      .navigationBarsPadding(),
  ) {
    HorizontalPager(
      state = pagerState,
      modifier = Modifier.weight(1f),
    ) { pageNumber ->
      when (pageNumber) {
        0 -> IntroPage1()
        1 -> IntroPage2()
        2 -> IntroPage3()
      }
    }
    PagerIndicator(
      state = pagerState,
      modifier = Modifier.padding(vertical = 20.dp),
    )
    SentinelLink(
      modifier = Modifier.clickable {
        context.openWeb("https://sentinel.co/")
      },
    )
    BasedButton(
      text = stringResource(
        when (pagerState.currentPage) {
          2 -> R.string.intro_btn_get_started
          else -> R.string.intro_btn_next
        },
      ),
      style = ButtonStyle.Primary,
      size = ButtonSize.M,
      onClick = {
        if (pagerState.currentPage < 2) {
          scope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
          }
        } else {
          navigateToDashboard.invoke()
        }
      },
      modifier = Modifier
        .padding(20.dp),
    )
  }
}

@Composable
private fun IntroPage1() {
  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize(),
  ) {
    Image(
      painter = painterResource(R.drawable.img_logo),
      contentDescription = null,
      modifier = Modifier.width(210.dp),
    )
    Spacer(modifier = Modifier.size(88.dp))
    Text(
      text = stringResource(R.string.intro_page1_title),
      color = Color.White,
      fontSize = 22.sp,
      fontWeight = FontWeight.W700,
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun IntroPage2() {
  Column(
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .padding(horizontal = 40.dp)
      .fillMaxSize(),
  ) {
    Image(
      painter = painterResource(R.drawable.img_logo),
      contentDescription = null,
      modifier = Modifier
        .width(74.dp)
        .align(Alignment.CenterHorizontally),
    )
    Spacer(modifier = Modifier.size(20.dp))
    Text(
      text = stringResource(R.string.intro_page2_title),
      color = Color.White,
      fontSize = 20.sp,
      fontWeight = FontWeight.W700,
    )
    Spacer(modifier = Modifier.size(10.dp))
    Text(
      text = stringResource(R.string.intro_page2_block1),
      color = Color(0xFF868D9B),
      fontSize = 16.sp,
      fontWeight = FontWeight.W400,
    )
    Spacer(modifier = Modifier.size(16.dp))
    Text(
      text = stringResource(R.string.intro_page2_block2),
      color = Color(0xFF868D9B),
      fontSize = 16.sp,
      fontWeight = FontWeight.W400,
    )
  }
}

@Composable
private fun IntroPage3() {
  Column(
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .padding(horizontal = 40.dp)
      .fillMaxSize(),
  ) {
    Image(
      painter = painterResource(R.drawable.img_logo),
      contentDescription = null,
      modifier = Modifier
        .width(74.dp)
        .align(Alignment.CenterHorizontally),
    )
    Spacer(modifier = Modifier.size(20.dp))
    Text(
      text = stringResource(R.string.intro_page3_title),
      color = Color.White,
      fontSize = 20.sp,
      fontWeight = FontWeight.W700,
    )
    Spacer(modifier = Modifier.size(10.dp))
    BulletBlock(
      text = stringResource(R.string.intro_page3_block1),
    )
    Spacer(modifier = Modifier.size(12.dp))
    BulletBlock(
      text = stringResource(R.string.intro_page3_block2),
    )
    Spacer(modifier = Modifier.size(12.dp))
    BulletBlock(
      text = stringResource(R.string.intro_page3_block3),
    )
  }
}

@Preview(
  showSystemUi = true,
  showBackground = true,
)
@Composable
private fun IntroPage1Preview() {
  IntroScreen {}
}
