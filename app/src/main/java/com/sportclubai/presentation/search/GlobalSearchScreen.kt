package com.sportclubai.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sportclubai.domain.model.SearchResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    navController: NavController,
    viewModel: GlobalSearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    TextField(
                        value = query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        placeholder = { Text("Search students, classes, etc.") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.search() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (query.isEmpty() && recentSearches.isNotEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recent Searches", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    recentSearches.forEach { search ->
                        Text(
                            text = search,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onQueryChange(search); viewModel.search() }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(results) { result ->
                        SearchResultCard(result)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(result: SearchResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(result.title, fontWeight = FontWeight.Bold)
            Text(result.subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}
