package com.example.githubstars.githubrepolist

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloCanceledException
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloHttpException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.apollographql.apollo.exception.ApolloParseException
import com.example.chromecustomtabsnavigator.findChromeCustomTabsNavigator
import com.example.githubstars.BaseFragment
import com.example.githubstars.MainActivity
import com.example.githubstars.R
import com.example.githubstars.RepositoriesQuery
import com.example.githubstars.data.ApolloConnector
import com.example.githubstars.data.GitHubRepo
import com.example.githubstars.databinding.FragmentGithubRepoListBinding
import com.example.githubstars.ui.BetweenItemDecoration

class GitHubRepoListFragment() : BaseFragment() {
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

        //TODO: don't show chips for potential searches or otherwise assume what user will search - but do autocomplete recent searches, starting with zero characters. Use Gmail as an example

        // TODO: multiple different view modes: initial load, recyclerview showing results, organization ID is not a match, organization has no repositories. Oh, and maybe if user exits searc hview? Oh, and no Internet connection or some other error
        // explore or star icons on first load?
        // offline_bolt if no connection? or error or warning or signal_cellular? probably cloud_off
        // warning for repository not found?
        // search for no repositories
        // animate changes in error messages?

        // TODO: progress indicator?

        // A SwipeRefreshLayout could be added, as seen in https://developer.android.com/training/swipe/add-swipe-interface
        // to provide pull-to-refresh functionality. But it's extremely unlikely for the results to change
        // significantly in a short amount of time, so a SwipeRefreshLayout is unjustified for this use case.

        //TODO: remember and suggest past searches? And maybe suggest some popular repos? Maybe I can use GraphQL to find popular repos?

//        setContentView(R.layout.activity_main)

        //TODO: temp
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

//        loadRepositories("spothero")


//        val appBar = findViewById<MaterialToolbar>(R.id.topAppBar)
//        appBar.menu.findItem(R.id.search).expandActionView()
//        val searchMenuItem = appBar.menu.findItem(R.id.search)


//        binding.topAppBar.setContentInsetsAbsolute(0, 0)
        //TODO: comment on synthetic references vs viewbinding
        //TODO: do I need this?
        binding.topAppBar.menu.findItem(R.id.search).expandActionView()
        val searchMenuItem = binding.topAppBar.menu.findItem(R.id.search)



        //TODO: can/should I prevent SearchView from collapsing?
        //TODO: I should only save valid searches (those with a matching organization ID)

        //TODO: accessibility

        // The X button fires onCloseListener (only if iconifiedbyDefault is true?) but the up button does not.

        val searchView = (searchMenuItem.actionView as SearchView)
        searchView.isActivated = true //TODO: is this necessary?
//        searchView.isIconifiedByDefault = false
        searchView.requestFocus()
//        searchView.isSubmitButtonEnabled = true
        //TODO: do I need to call setActivated ?
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

        //TODO: when SearchView opens, automatically put focus in it

        // Doesn't fire
        searchView.setOnCloseListener {
            Log.e(MainActivity.TAG, "searchView onCloseListener()")
            false
        }
        // This fires when searchView closes. It doesn't fire when it opens, not unless user also puts focus in it.  I think it would fire if user clicked in a different txt field on screen
        searchView.setOnQueryTextFocusChangeListener { v, hasFocus ->
            Log.e(MainActivity.TAG, "searchView onQueryTextFocusChangeListener(v: $v, hasFocus: $hasFocus")
        }
        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                Log.e(MainActivity.TAG, "searchView.onSuggestionListener onSuggestionSelect($position)")
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                Log.e(MainActivity.TAG, "searchView.onSuggestionListener onSuggestionClick($position)")
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
        Log.e("xxx", "loadRepositories($organizationLogin)")
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
                            //TODO: does `showOneOf` provide value or should it be removed?
                            showOneOf(binding.recyclerView, binding.noReposLayout, gitHubRepoList.size > 0)


                            //TODO: rename viewAdapter?
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
                            } else {
                                //TODO: need to figure out how to tell if there was an error, zreo repos, or if the organization name was invalid
                                //TODO: see if I can eliminate `!!`
                                binding.noReposImageView.setImageDrawable(ContextCompat.getDrawable(activity!!, R.drawable.ic_search_black_24dp))
                                binding.noReposTextView.text = getString(R.string.no_repos_organization_has_none, organizationLogin)
                            }

                        }
                    }
                    Log.d(
                        MainActivity.TAG,
                        "Response: " + response.data()?.organization()?.repositories()?.edges()?.size
                    )
                }

                override fun onFailure(e: ApolloException) {
                    //TODO: does `showOneOf` provide value or should it be removed?
                    showOneOf(binding.recyclerView, binding.noReposLayout, false)
                    binding.noReposImageView.setImageDrawable(ContextCompat.getDrawable(activity!!, R.drawable.ic_cloud_off_black_24dp))
                    binding.noReposTextView.text = getString(R.string.no_repos_connection_error)

                    Log.d(MainActivity.TAG, "Exception " + e.message, e)
                }

                // TODO: do I need this method?
                override fun onCanceledError(e: ApolloCanceledException) {
                    super.onCanceledError(e)
                    Log.d(MainActivity.TAG, "OnCanceledError " + e.message, e)
                }

                // TODO: do I need this method?
                override fun onNetworkError(e: ApolloNetworkException) {
                    super.onNetworkError(e)
                    Log.d(MainActivity.TAG, "OnNetworkError " + e.message, e)
                }

                // TODO: do I need this method?
                override fun onParseError(e: ApolloParseException) {
                    super.onParseError(e)
                    Log.d(MainActivity.TAG, "OnParseError " + e.message, e)
                }

                // TODO: do I need this method?
                override fun onHttpError(e: ApolloHttpException) {
                    super.onHttpError(e)
                    Log.d(MainActivity.TAG, "OnHttpError " + e.message, e)
                }

                // TODO: do I need this method?
                override fun onStatusEvent(event: ApolloCall.StatusEvent) {
                    super.onStatusEvent(event)
                    Log.d(MainActivity.TAG, "OnStatusEvent: $event")
                }
            })
    }

    companion object {
        const val TAG = "GitHubRepoListFragment"
    }
}
