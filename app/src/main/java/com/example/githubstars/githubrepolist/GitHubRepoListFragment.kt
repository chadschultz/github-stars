package com.example.githubstars.githubrepolist

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.chromecustomtabsnavigator.findChromeCustomTabsNavigator
import com.example.githubstars.MainActivity
import com.example.githubstars.R
import com.example.githubstars.RepositoriesQuery
import com.example.githubstars.data.ApolloConnector
import com.example.githubstars.data.GitHubRepo
import com.example.githubstars.databinding.FragmentGithubRepoListBinding
import com.example.githubstars.ui.BetweenItemDecoration

class GitHubRepoListFragment() : Fragment() {
    // Strange syntax, but taken directly from
    // https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: FragmentGithubRepoListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewAdapter: GitHubRepoAdapter
    private lateinit var viewManager: LinearLayoutManager

    // I like to order lifecycle functions in lifecycle order
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGithubRepoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: progress indicator?

        // A SwipeRefreshLayout could be added, as seen in https://developer.android.com/training/swipe/add-swipe-interface
        // to provide pull-to-refresh functionality. But it's extremely unlikely for the results to change
        // significantly in a short amount of time, so a SwipeRefreshLayout is unjustified for this use case.

        //TODO: remember and suggest past searches? And maybe suggest some popular repos? Maybe I can use GraphQL to find popular repos?

//        setContentView(R.layout.activity_main)

        //TODO: temp
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

//        loadRepositories("spothero")

        //TODO: view binding

//        val appBar = findViewById<MaterialToolbar>(R.id.topAppBar)
//        appBar.menu.findItem(R.id.search).expandActionView()
//        val searchMenuItem = appBar.menu.findItem(R.id.search)

        //TODO: comment on synthetic references vs viewbinding
        binding.topAppBar.menu.findItem(R.id.search).expandActionView()
        val searchMenuItem = binding.topAppBar.menu.findItem(R.id.search)

        val searchView = (searchMenuItem.actionView as SearchView)
        searchView.isActivated = true //TODO: is this necessary?
        searchView.isIconifiedByDefault = false
        searchView.requestFocus()
//        searchView.isSubmitButtonEnabled = true
        //TODO: do I need to call setActivated ?
        //TODO: Rx debouncing?
        searchView.queryHint = getString(R.string.search_query_hint)
        //TODO: if I press enter, it searches twice. If I tap the search icon, it searches once
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.e(MainActivity.TAG, "onQueryTextSubmit($query)")
                loadRepositories(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.e(MainActivity.TAG, "onQueryTextChange($newText)")
                //TODO: should I do anything with this?
                return false
            }
        })

//        binding.navHostFragment

        //TODO: context val?
        viewManager = LinearLayoutManager(requireContext())
        viewAdapter = GitHubRepoAdapter()

        val decoration =
            BetweenItemDecoration(requireContext())
        getDrawable(requireContext(),
            R.drawable.divider_item_decoration
        )?.let {
            decoration.drawable = it
        }
        binding.recyclerView.apply {
            layoutManager = viewManager
            adapter = viewAdapter
            addItemDecoration(decoration)
        }

        //TODO: improve how I set mayLaunchUrl



    }

    override fun onStart() {
        super.onStart()
        findChromeCustomTabsNavigator().bindCustomTabsService()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //TODO: can't do autocomplete exactly, but perhaps I should have a match pop up for the user to tap when there's an exact match?

    private fun loadRepositories(organizationLogin: String) {
        ApolloConnector.setupApollo().query(
            RepositoriesQuery.builder()
                .organizationLogin(organizationLogin)
                .build()
        )
            .enqueue(object : ApolloCall.Callback<RepositoriesQuery.Data?>() {
                override fun onResponse(response: Response<RepositoriesQuery.Data?>) {
                    response.data()?.organization()?.repositories()?.edges()?.let { edgesList ->
                        //TODO: what's the right capitalization?
                        val gitHubRepoList = mutableListOf<GitHubRepo>()
                        for (repoEdge in edgesList) {
                            repoEdge?.node()?.let { node ->
                                var url: Uri? = null
                                try {
                                    url = Uri.parse(node.url().toString())
                                } catch (e: Exception) {
                                    // If a URL is missing or malformed, report the error and keep going
                                    // the user just won't be able to click the repository name.
                                    Log.e(MainActivity.TAG, "Invalid URL in repo node $node")
                                }
                                gitHubRepoList.add(
                                    GitHubRepo(
                                        id = node.id(),
                                        name = node.name(),
                                        description = node.description() ?: "",
                                        url = url,
                                        starCount = node.stargazers().totalCount()
                                    )
                                )
                            }
                        }
                        // TODO: is there a better way?
                        activity?.runOnUiThread() {
                            viewAdapter.submitList(gitHubRepoList)
                            //TODO: likely a better way to do this
                            if (gitHubRepoList.size >= 1) {
                                gitHubRepoList[0].url?.let {url ->
                                    // Per https://developer.chrome.com/multidevice/android/customtabs#pre-render-content,
                                    // we should only use mayLaunchUrl if the odds are at least 50% of clicking the link.
                                    // As much as I'd like to load up all three URLs, even the URL for the most starred
                                    // repository is tricky to justify given that guidance.
                                    findChromeCustomTabsNavigator().mayLaunchUrl(url)
                                }
                            }
                        }
                    }
                    Log.d(
                        MainActivity.TAG,
                        "Response: " + response.data()?.organization()?.repositories()?.edges()?.size
                    )
                }

                override fun onFailure(e: ApolloException) {
                    Log.d(MainActivity.TAG, "Exception " + e.message, e)
                }

            })
    }

    companion object {
        const val TAG = "GitHubRepoListFragment"
    }
}
