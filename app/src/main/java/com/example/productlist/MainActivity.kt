package com.example.productlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.productlist.ui.productlist.ProductListRoute
import com.example.productlist.ui.productlist.ProductListViewModel
import com.example.productlist.ui.productlist.ProductListViewModelFactory

/**
 * Single-screen host Activity. Its only jobs are (1) obtain the
 * ViewModel via the app-level repository and (2) set the Compose
 * content -- all actual screen logic lives in ProductListRoute/Screen
 * and ProductListViewModel.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: ProductListViewModel by viewModels {
        ProductListViewModelFactory((application as ProductListApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ProductListRoute(viewModel = viewModel)
                }
            }
        }
    }
}
