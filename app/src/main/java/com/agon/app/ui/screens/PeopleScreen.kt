package com.agon.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agon.app.ui.components.EmptyState
import com.agon.app.ui.components.PersonRow
import com.agon.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleScreen(vm: AppViewModel, onOpen: (String) -> Unit) {
    val s by vm.strings.collectAsState()
    val people by vm.people.collectAsState()
    var q by remember { mutableStateOf("") }
    val live = vm.livePeople().filter { p ->
        val hay = listOf(p.displayName, p.handle, p.city, p.tribe, p.region, p.bio).joinToString(" ").lowercase()
        q.isBlank() || hay.contains(q.trim().lowercase())
    }

    Scaffold(topBar = { TopAppBar(title = { Text(s.people) }) }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            OutlinedTextField(
                value = q,
                onValueChange = { q = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                placeholder = { Text(s.searchPeople) },
                singleLine = true,
            )
            if (live.isEmpty()) {
                EmptyState(s.people, if (q.isBlank()) s.emptyPeople else s.noResults)
            } else {
                LazyColumn {
                    items(live, key = { it.id }) { p ->
                        val sub = listOf(
                            if (p.online) s.onlineNow else p.region,
                            p.tribe,
                            p.language,
                        ).filter { it.isNotBlank() }.joinToString(" · ")
                        PersonRow(p, sub) { onOpen(p.id) }
                    }
                }
            }
        }
    }
}
