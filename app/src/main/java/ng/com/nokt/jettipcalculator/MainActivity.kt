package ng.com.nokt.jettipcalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ng.com.nokt.jettipcalculator.components.InputField
import ng.com.nokt.jettipcalculator.ui.theme.JetTipCalculatorTheme
import ng.com.nokt.jettipcalculator.util.calculateTotalPerPerson
import ng.com.nokt.jettipcalculator.util.calculateTotalTip
import ng.com.nokt.jettipcalculator.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                //TopHeader()
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit){
    JetTipCalculatorTheme {
        content()
    }
}

@Composable
fun TopHeader(totalPerson: Double = 234.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp)
        .height(150.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerson)
            Text(
                text = "Total per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent(){

    val splitByState = remember {
        mutableStateOf(1)
    }

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    val range = IntRange(start = 1, endInclusive = 100)

    BillForm(
        splitByState = splitByState,
        tipAmountState = tipAmountState,
        totalPerPersonState = totalPerPersonState
    ){}

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier,
             range: IntRange = 1..100,
             tipAmountState: MutableState<Double>,
             splitByState: MutableState<Int>,
             totalPerPersonState: MutableState<Double>,
             onValChange:(String)-> Unit = {}
             ){
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()

    Column{
        TopHeader(totalPerson = totalPerPersonState.value)
        Surface(
            modifier = modifier
                .padding(2.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(corner = CornerSize(12.dp)))
                .border(BorderStroke(1.dp, Color.LightGray))
        ) {
            Column(
                modifier = modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                InputField(
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())

                        keyboardController?.hide()
                    }
                )
                //if (validState){
                    Row(modifier = modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start) {
                        Text(text = "Split",
                        modifier = modifier.align(
                            alignment = Alignment.CenterVertically
                        ))
                        Spacer(modifier = modifier.width(120.dp))

                        Row(modifier = modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End) {
                            RoundIconButton(
                                imageVector = Icons.Default.Remove,
                                onClick = {
                                    splitByState.value =
                                        if (splitByState.value > 1) splitByState.value - 1
                                    else 1

                                    totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value, tipPercentage = tipPercentage)
                                }
                            )

                            Text(text = "${splitByState.value}", modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(5.dp))

                            RoundIconButton(
                                imageVector = Icons.Default.Add,
                                onClick = {
                                    if (splitByState.value < range.last){
                                        splitByState.value += 1

                                        totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                            splitBy = splitByState.value, tipPercentage = tipPercentage)
                                    }
                                     }
                            )

                        }
                    }

                //Tip Row
                Row(modifier = modifier.padding(horizontal = 3.dp, vertical = 12.dp)){
                    Text(text = "Tip",
                        modifier = modifier.align(alignment = Alignment.CenterVertically))
                        Spacer(modifier = modifier.width(200.dp))
                    Text(text = "$${tipAmountState.value}",
                        modifier = modifier.align(alignment = Alignment.CenterVertically))
                }

                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$tipPercentage%")
                    Spacer(modifier = modifier.height(10.dp))

                    Slider(value = sliderPositionState.value, onValueChange = { newVal ->
                        sliderPositionState.value = newVal

                        tipAmountState.value = calculateTotalTip(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage)
                        totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                        splitBy = splitByState.value, tipPercentage = tipPercentage)
                        Log.d("Slider", "BillForm: $newVal")
                    },
                    modifier = modifier.padding(start = 16.dp, end = 16.dp),
                    steps = 5)
                }
    //            }else{
    //                Box(){}
    //            }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
        Text(text = "Hello Compose")
    }
}