package eu.kanade.tachiyomi.ui.animecategory

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import eu.kanade.domain.category.interactor.CreateAnimeCategoryWithName
import eu.kanade.domain.category.interactor.DeleteAnimeCategory
import eu.kanade.domain.category.interactor.GetAnimeCategories
import eu.kanade.domain.category.interactor.RenameAnimeCategory
import eu.kanade.domain.category.interactor.ReorderAnimeCategory
import eu.kanade.domain.category.model.Category
import eu.kanade.tachiyomi.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class AnimeCategoryScreenModel(
    private val getCategories: GetAnimeCategories = Injekt.get(),
    private val createCategoryWithName: CreateAnimeCategoryWithName = Injekt.get(),
    private val deleteCategory: DeleteAnimeCategory = Injekt.get(),
    private val reorderCategory: ReorderAnimeCategory = Injekt.get(),
    private val renameCategory: RenameAnimeCategory = Injekt.get(),
) : StateScreenModel<AnimeCategoryScreenState>(AnimeCategoryScreenState.Loading) {

    private val _events: Channel<CategoryEvent> = Channel()
    val events = _events.consumeAsFlow()

    init {
        coroutineScope.launch {
            getCategories.subscribe()
                .collectLatest { categories ->
                    mutableState.update {
                        AnimeCategoryScreenState.Success(
                            categories = categories.filterNot(Category::isSystemCategory),
                        )
                    }
                }
        }
    }

    fun createCategory(name: String) {
        coroutineScope.launch {
            when (createCategoryWithName.await(name)) {
                is CreateAnimeCategoryWithName.Result.InternalError -> _events.send(CategoryEvent.InternalError)
                CreateAnimeCategoryWithName.Result.NameAlreadyExistsError -> _events.send(CategoryEvent.CategoryWithNameAlreadyExists)
                CreateAnimeCategoryWithName.Result.Success -> {}
            }
        }
    }

    fun deleteCategory(categoryId: Long) {
        coroutineScope.launch {
            when (deleteCategory.await(categoryId = categoryId)) {
                is DeleteAnimeCategory.Result.InternalError -> _events.send(CategoryEvent.InternalError)
                DeleteAnimeCategory.Result.Success -> {}
            }
        }
    }

    fun moveUp(category: Category) {
        coroutineScope.launch {
            when (reorderCategory.await(category, category.order - 1)) {
                is ReorderAnimeCategory.Result.InternalError -> _events.send(CategoryEvent.InternalError)
                ReorderAnimeCategory.Result.Success -> {}
                ReorderAnimeCategory.Result.Unchanged -> {}
            }
        }
    }

    fun moveDown(category: Category) {
        coroutineScope.launch {
            when (reorderCategory.await(category, category.order + 1)) {
                is ReorderAnimeCategory.Result.InternalError -> _events.send(CategoryEvent.InternalError)
                ReorderAnimeCategory.Result.Success -> {}
                ReorderAnimeCategory.Result.Unchanged -> {}
            }
        }
    }

    fun renameCategory(category: Category, name: String) {
        coroutineScope.launch {
            when (renameCategory.await(category, name)) {
                is RenameAnimeCategory.Result.InternalError -> _events.send(CategoryEvent.InternalError)
                RenameAnimeCategory.Result.NameAlreadyExistsError -> _events.send(CategoryEvent.CategoryWithNameAlreadyExists)
                RenameAnimeCategory.Result.Success -> {}
            }
        }
    }

    fun showDialog(dialog: CategoryDialog) {
        mutableState.update {
            when (it) {
                AnimeCategoryScreenState.Loading -> it
                is AnimeCategoryScreenState.Success -> it.copy(dialog = dialog)
            }
        }
    }

    fun dismissDialog() {
        mutableState.update {
            when (it) {
                AnimeCategoryScreenState.Loading -> it
                is AnimeCategoryScreenState.Success -> it.copy(dialog = null)
            }
        }
    }
}

sealed class CategoryDialog {
    object Create : CategoryDialog()
    data class Rename(val category: Category) : CategoryDialog()
    data class Delete(val category: Category) : CategoryDialog()
}

sealed class CategoryEvent {
    sealed class LocalizedMessage(@StringRes val stringRes: Int) : CategoryEvent()
    object CategoryWithNameAlreadyExists : LocalizedMessage(R.string.error_category_exists)
    object InternalError : LocalizedMessage(R.string.internal_error)
}

sealed class AnimeCategoryScreenState {

    @Immutable
    object Loading : AnimeCategoryScreenState()

    @Immutable
    data class Success(
        val categories: List<Category>,
        val dialog: CategoryDialog? = null,
    ) : AnimeCategoryScreenState() {

        val isEmpty: Boolean
            get() = categories.isEmpty()
    }
}
