import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import javax.swing.JFileChooser

@Composable
@Preview
fun App() {

    MaterialTheme {
        ChooseView()
    }
}


@Composable
fun ChooseView() {
    var excelPath by remember { mutableStateOf("excel文件地址") }
    var xmlFilesPath by remember { mutableStateOf("文件夹地址") }
    var logInfo by remember { mutableStateOf("") }
    val logShareFlow = MutableStateFlow("")
//    val logInfo: MutableState<String> = mutableStateOf("")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                try {
                    JFileChooser().apply {
                        fileSelectionMode = JFileChooser.FILES_ONLY
                        if (showOpenDialog(ComposeWindow()) == JFileChooser.APPROVE_OPTION) {
                            excelPath = selectedFile.absolutePath
                        }
                    }
                    // Place definition above class declaration to make field static
                } catch (e: Exception) {
                    e.printStackTrace()
                    println(e.message)
                }
            }) {
                Text("选择Excel文件")
            }
            Spacer(Modifier.size(20.dp))
            RoundedBorderText(
                text = excelPath,
                borderColor = Color.Blue,
                backgroundColor = Color.White,
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 20.dp, end = 20.dp).sizeIn(minWidth = 100.dp)
            )
        }
        Spacer(Modifier.size(40.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {

                JFileChooser().apply {
                    fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    if (showOpenDialog(ComposeWindow()) == JFileChooser.APPROVE_OPTION) {
                        xmlFilesPath = selectedFile.absolutePath
                    }
                }
                // Place definition above class declaration to make field static

            }) {
                Text("选择Xml文件夹")
            }
            Spacer(Modifier.size(20.dp))
            RoundedBorderText(
                text = xmlFilesPath,
                borderColor = Color.Blue,
                backgroundColor = Color.White,
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 20.dp, end = 20.dp).sizeIn(minWidth = 100.dp)
            )
        }
        Spacer(Modifier.size(20.dp))
        Button(onClick = {
            try {
                runBlocking {
                    ParseUtils.excel2Xml(excelPath, xmlFilesPath, logShareFlow)
//
//                    logInfo = logShareFlow.collectAsState().value
//                    println(logShareFlow.collectAsState().value)
//                    logShareFlow.collect {
//                        println("---->$it")
//                        logInfo = it
//                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println(e.message)
            }
        }) {
            Text("开始转化")
        }
        Spacer(Modifier.size(20.dp))
        Column(modifier = Modifier.size(width = 400.dp, height = Dp.Unspecified)) {
            Text(
                text = logInfo,
                modifier = Modifier
                    .fillMaxSize()
                    .border(BorderStroke(1.dp, Color.Blue), RoundedCornerShape(8.dp))
                    .padding(4.dp),
            )
        }
        Spacer(Modifier.size(40.dp))

    }
}

@Composable
fun ParentView() {

}

@Composable
fun RoundedBorderTextField(
    modifier: Modifier = Modifier,
    shape: Shape,
    borderColor: Color,
    textFieldValue: MutableState<String>
) {
    OutlinedTextField(
        value = textFieldValue.value,
        onValueChange = { textFieldValue.value = it },
        modifier = modifier
            .border(BorderStroke(1.dp, borderColor), shape)
            .padding(4.dp),
        textStyle = MaterialTheme.typography.body1,
        shape = shape
    )
}

@Composable
fun RoundedBorderText(
    text: String,
    borderColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(10.dp),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = backgroundColor
    ) {
        Box(modifier.padding(10.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.body1,
                color = Color.Black
            )
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "excel转string工具") {
        App()
    }
}

// This is what happens when you write the above ^^^