import androidx.compose.desktop.ui.tooling.preview.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.awt.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import kotlinx.coroutines.*
import javax.swing.*

@Composable
@Preview
fun App() {
    MaterialTheme {

        var logInfo by remember { mutableStateOf("") }

        ChooseView(
            startClick = { excelPath, xmlFilesPath ->
                try {
                    runBlocking {
                        ParseUtils.excel2Xml(excelPath, xmlFilesPath) {
                            logInfo = buildString {
                                append(logInfo)
                                append(it)
                                append("\n")
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    logInfo = buildString {
                        append(logInfo)
                        append(e.message)
                        append("\n")
                    }
                    println(e.message)
                }
            }, logInfo, modifier = Modifier
        )
    }
}

@Composable
fun ChooseView(
    startClick: ((String, String) -> Unit)? = null, logInfo: String, modifier: Modifier = Modifier
) {
    var excelPath by remember { mutableStateOf("excel文件地址") }
    var xmlFilesPath by remember { mutableStateOf("文件夹地址") }
    Column(
        horizontalAlignment = Alignment.Start, modifier = modifier.padding(20.dp).fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = modifier
        ) {
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
                text = excelPath, borderColor = Color.Blue, backgroundColor = Color.White, modifier = Modifier.align(Alignment.CenterVertically).fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp).sizeIn(minWidth = 100.dp)
            )
        }
        Spacer(Modifier.size(10.dp))
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
                text = xmlFilesPath, borderColor = Color.Blue, backgroundColor = Color.White, modifier = Modifier.align(Alignment.CenterVertically).fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp).sizeIn(minWidth = 100.dp)
            )
        }
        Spacer(Modifier.size(20.dp))
        Button(
            onClick = {
                startClick?.invoke(excelPath, xmlFilesPath)
            },
        ) {
            Text("开始转化", fontSize = TextUnit(20f, TextUnitType.Sp))
        }
        Spacer(Modifier.size(20.dp))

        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .border(BorderStroke(1.dp, Color.Blue), RoundedCornerShape(8.dp))
        ) {

            Text(
                text = logInfo,
            )

        }
        Spacer(Modifier.size(40.dp))

    }
}

@Composable
fun RoundedBorderText(
    text: String, borderColor: Color, backgroundColor: Color, fontSize: TextUnit = TextUnit.Unspecified, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, border = BorderStroke(1.dp, borderColor), shape = RoundedCornerShape(8.dp), backgroundColor = backgroundColor
    ) {
        Box(modifier) {
            Text(
                text = text, style = MaterialTheme.typography.body1, color = Color.Black, fontSize = fontSize
            )
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "excel转string工具", resizable = false) {
        App()
    }
}

// This is what happens when you write the above ^^^