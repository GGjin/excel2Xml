import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.runBlocking
import javax.swing.JFileChooser

@Composable
@Preview
fun App() {

    MaterialTheme {

        var excelPath by remember { mutableStateOf("excel文件地址") }
        var xmlFilesPath by remember { mutableStateOf("文件夹地址") }

        ChooseView(excelPath, excelPathCallBack = { path ->
            excelPath = path
        }, xmlFilesPath, xmlFilesPathCallBack = {
            xmlFilesPath = it
        }, modifier = Modifier
            .background(Color.Blue)
        )
    }
}

@Composable
fun ChooseView(
    excelPath: String, excelPathCallBack: ((String) -> Unit)? = null, xmlFilesPath: String, xmlFilesPathCallBack: ((String) -> Unit)? = null, modifier: Modifier = Modifier
) {

    var logInfo by remember { mutableStateOf("") }
    val logList by lazy { mutableListOf<String>() }
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier.padding(20.dp)
            .background(Color.Red),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Button(onClick = {
                try {
                    JFileChooser().apply {
                        fileSelectionMode = JFileChooser.FILES_ONLY
                        if (showOpenDialog(ComposeWindow()) == JFileChooser.APPROVE_OPTION) {
                            excelPathCallBack?.invoke(selectedFile.absolutePath)
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
                text = excelPath, borderColor = Color.Blue, backgroundColor = Color.White, modifier = Modifier.align(Alignment.CenterVertically).padding(start = 20.dp, end = 20.dp).sizeIn(minWidth = 100.dp)
            )
        }
        Spacer(Modifier.size(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {

                JFileChooser().apply {
                    fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    if (showOpenDialog(ComposeWindow()) == JFileChooser.APPROVE_OPTION) {
                        xmlFilesPathCallBack?.invoke(selectedFile.absolutePath)
                    }
                }
                // Place definition above class declaration to make field static

            }) {
                Text("选择Xml文件夹")
            }
            Spacer(Modifier.size(20.dp))
            RoundedBorderText(
                text = xmlFilesPath, borderColor = Color.Blue, backgroundColor = Color.White, modifier = Modifier.align(Alignment.CenterVertically).padding(start = 20.dp, end = 20.dp).sizeIn(minWidth = 100.dp)
            )
        }
        Spacer(Modifier.size(20.dp))
        Button(
            onClick = {
                try {
                    runBlocking {
                        ParseUtils.excel2Xml(excelPath, xmlFilesPath) {
                            logInfo = it
                            logList.add(it)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    println(e.message)
                }
            },
            modifier = modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("开始转化")
        }
        Spacer(Modifier.size(20.dp))


        Column(
            modifier = Modifier
                .size(width = 400.dp, height = Dp.Unspecified)
                .border(BorderStroke(1.dp, Color.Blue), RoundedCornerShape(8.dp))
        ) {
            LazyColumn() {
                items(logList) { log ->
                    Text(
                        text = log,
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                    )
                }
            }
        }
        Spacer(Modifier.size(40.dp))

    }
}

@Composable
fun RoundedBorderText(
    text: String, borderColor: Color, backgroundColor: Color, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(10.dp), border = BorderStroke(1.dp, borderColor), shape = RoundedCornerShape(8.dp), backgroundColor = backgroundColor
    ) {
        Box(modifier.padding(10.dp)) {
            Text(
                text = text, style = MaterialTheme.typography.body1, color = Color.Black
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