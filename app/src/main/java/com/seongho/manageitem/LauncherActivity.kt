package com.seongho.manageitem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.seongho.manageitem.ui.theme.ManageItemTheme

import android.content.Intent

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 여기에 스플래시 로직, 초기화 로직 등을 넣을 수 있습니다.
        // 작업 완료 후 MainActivity로 이동
        startActivity(Intent(this, MainActivity::class.java))
        finish() // LauncherActivity는 종료하여 백스택에 남지 않도록 함
    }
}




//class LauncherActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            ManageItemTheme {
////                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
////                    Greeting(
////                        name = "Android",
////                        modifier = Modifier.padding(innerPadding)
////                    )
////                }
//                layout(modifier = Modifier)
//            }
//        }
//    }
//}
//
//@Composable
//fun layout(modifier: Modifier = Modifier) {
//    Box(
//        modifier = modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Button(onClick = {
//            println("중앙 버튼 클릭됨!")
//        }) {
//            Text("화면 중앙 버튼")
//        }
//    }
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    ManageItemTheme {
//        Greeting("Android")
//    }
//}