    package com.dicoding.storyapphanif.ui.main

    import androidx.arch.core.executor.testing.InstantTaskExecutorRule
    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.paging.AsyncPagingDataDiffer
    import androidx.paging.PagingData
    import androidx.paging.PagingSource
    import androidx.paging.PagingState
    import androidx.recyclerview.widget.ListUpdateCallback
    import com.dicoding.storyapphanif.DataDummy
    import com.dicoding.storyapphanif.LiveDataTestUtil.getOrAwaitValue
    import com.dicoding.storyapphanif.MainDispatcherRule
    import com.dicoding.storyapphanif.data.UserRepository
    import com.dicoding.storyapphanif.data.retrofit.response.ListStoryItem
    import com.dicoding.storyapphanif.ui.adapter.StoryListAdapter
    import junit.framework.TestCase.assertEquals
    import junit.framework.TestCase.assertNotNull
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.ExperimentalCoroutinesApi
    import kotlinx.coroutines.test.runTest
    import org.junit.Rule
    import org.junit.Test
    import org.junit.runner.RunWith
    import org.mockito.Mock
    import org.mockito.Mockito
    import org.mockito.junit.MockitoJUnitRunner


    @ExperimentalCoroutinesApi
    @RunWith(MockitoJUnitRunner::class)
    class MainViewModelTest {

        @get:Rule
        val instantExecutorRule = InstantTaskExecutorRule()

        @get:Rule
        val mainDispatcherRules = MainDispatcherRule()

        @Mock
        private lateinit var userRepository: UserRepository
        private val dummyStories = DataDummy.generateDummyStoryResponse()
        private val tokenDummy =
            " eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlqNXBjX0xBUkNfQWdLNjEiLCJpYXQiOjE2NDE3OTk5NDl9.flEMaQ7zsdYkxuyGbiXjEDXO8kuDTcI__3UjCwt6R_I"

        @Test
        fun `when Get Stories Should Not Null and Return Data`() = runTest {
            val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStories)
            val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
            expectedStories.value = data


            Mockito.`when`(userRepository.getStory(tokenDummy)).thenReturn(expectedStories)

            val mainViewModel = MainViewModel(userRepository)
            val actualStories: PagingData<ListStoryItem> =
                mainViewModel.getStory(tokenDummy).getOrAwaitValue()

            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryListAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main

            )
            differ.submitData(actualStories)
            assertNotNull(differ.snapshot())
            assertEquals(dummyStories.size, differ.snapshot().size)
            assertEquals(dummyStories[0], differ.snapshot()[0])
        }

        @Test
        fun `when Get Story Empty Should Return No Data`() = runTest {
            val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
            val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
            expectedStories.value = data
            Mockito.`when`(userRepository.getStory(tokenDummy)).thenReturn(expectedStories)
            val mainViewModel = MainViewModel(userRepository)
            val actualStories: PagingData<ListStoryItem> =
                mainViewModel.getStory(tokenDummy).getOrAwaitValue()
            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryListAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main,
            )
            differ.submitData(actualStories)

            assertEquals(0, differ.snapshot().size)
        }

    }
        class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
        companion object {
            fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int? {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }

        private val noopListUpdateCallback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {

            }

            override fun onRemoved(position: Int, count: Int) {

            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {

            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {

            }
        }




