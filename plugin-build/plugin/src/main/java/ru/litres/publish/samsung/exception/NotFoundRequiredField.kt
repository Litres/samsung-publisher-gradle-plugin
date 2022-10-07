package ru.litres.publish.samsung.exception

class NotFoundRequiredField(fieldName: String) : Exception("Not found required field \"$fieldName\"")
