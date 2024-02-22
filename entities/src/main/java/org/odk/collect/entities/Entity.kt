package org.odk.collect.entities

data class Entity(val dataset: String, val id: String, val label: String, val properties: List<Pair<String, String>>)
