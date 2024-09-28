package com.example.tommyho_multi_paneshoppingapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tommyho_multi_paneshoppingapp.ui.theme.TommyHo_MultiPaneShoppingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TommyHo_MultiPaneShoppingAppTheme {
                AdaptiveLayout()
            }
        }
    }
}

@Composable
fun AdaptiveLayout() {
    val windowInfo = calculateCurrentWindowInfo()
    val productList = listOf(
        Product("Apples", "Red circular fruit", 2.99),
        Product("Banana", "Yellow fruit", 0.16),
        Product("Orange", "Orange circular fruit", 3.29),
        Product("Strawberry","Red, heart-shaped fruit", 2.85),
        Product("Blueberries", "Small, blue, circular fruit", 4.95))
    var selectedItem by rememberSaveable(stateSaver = ProductSaver) { mutableStateOf<Product?>(null) }

    if (windowInfo.orientation == Orientation.PORTRAIT) {
        Log.d("AdaptiveLayoutPortrait", "Recomposing with task: ${selectedItem?.name}")
        PortraitLayout(windowInfo, productList, selectedItem) { newItem ->
            selectedItem = newItem
        }
    } else {
        Log.d("AdaptiveLayoutLandscape", "Recomposing with task: ${selectedItem?.name}")
        LandscapeLayout(windowInfo, productList, selectedItem) { newItem ->
            selectedItem = newItem
        }
    }
}
@Composable
fun PortraitLayout(windowInfo: WindowInfo, productList: List<Product>, selectedItem: Product?, onItemSelected: (Product?) -> Unit) {
    //This is when the phone is vertical
    if(selectedItem == null){
        ProductList(productList, onItemSelected,modifier = Modifier.fillMaxSize())
    }
    else{
        ProductDetailPane(task = selectedItem,onItemSelected,  modifier = Modifier.fillMaxSize())
    }

}
@Composable
fun ProductList(itemList: List<Product>, onItemSelected: (Product?) -> Unit, modifier: Modifier = Modifier){
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // List Title
        Text(
            text = "Items",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            // List Items
            items(itemList.size) { index ->
                Text(
                    text = itemList[index].name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemSelected(itemList[index]) }
                        .padding(8.dp),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun ProductDetailPane(task: Product?, onItemSelected: (Product?) -> Unit, modifier: Modifier = Modifier) {
    Log.d("ProductDetailPane", "Recomposing with task: ${task?.name}")
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (task != null) {
            // Task Detail
            Text(
                text = "Details for ${task.name}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Description: ${task.description}",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Price: $${task.price}",
                fontSize = 16.sp
            )
        } else {
            // No task selected
            Text(
                text = "Select a product to view details.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Button(onClick = { onItemSelected(null) }) {
            Text(text = "Clear Selected")
        }
    }
}
@Composable
fun LandscapeLayout(windowInfo: WindowInfo, productList:List<Product>, selectedItem: Product?, onItemSelected: (Product?) -> Unit){
    //This is when the phone is horizontal
    Row(modifier = Modifier.fillMaxSize()) {
        Log.d("LandscapeLayout", "Recomposing with task: ${selectedItem?.name}")
        ProductList(productList, onItemSelected, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(16.dp))
        ProductDetailPane(selectedItem, onItemSelected, modifier = Modifier.weight(1f))
    }

}

@Composable
fun calculateCurrentWindowInfo(): WindowInfo {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val orientation = if (configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
        Orientation.PORTRAIT
    } else {
        Orientation.LANDSCAPE
    }
    return WindowInfo(
        widthDp = screenWidth,
        heightDp = screenHeight,
        orientation = orientation
    )
}


data class WindowInfo(
    val widthDp: Int,
    val heightDp: Int,
    val orientation: Orientation
)


data class Product(
    val name: String,
    val description: String,
    val price: Double
)

//This productSaver I got from Gemini in order to correctly implement rememberSavable on my Product class, since rememberSavable generally only works on primitive data types.
object ProductSaver : Saver<Product?, Any> {
    override fun restore(value: Any): Product? {
        if (value is List<*> && value.size == 3) {
            val name = value[0] as String
            val description = value[1] as String
            val price = value[2] as Double
            return Product(name, description, price)
        } else {
            return null // Handle cases where data is invalid
        }
    }

    override fun SaverScope.save(value: Product?): Any {
        return listOfNotNull(value?.name, value?.description, value?.price)
    }
}

enum class Orientation {
    PORTRAIT,
    LANDSCAPE
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TommyHo_MultiPaneShoppingAppTheme {

    }
}