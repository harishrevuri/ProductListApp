package com.example.productlist.ui.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.productlist.data.repository.ProductRepository

/**
 * Minimal manual-DI factory. No Hilt/Koin is wired up for this 30-minute
 * exercise; in a production app this would be replaced by
 * @HiltViewModel + @Inject constructor on ProductListViewModel.
 */
class ProductListViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
            "Unknown ViewModel class: $modelClass"
        }
        return ProductListViewModel(repository) as T
    }
}
