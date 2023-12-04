fun String.splitOnSpaces() = this.split("\\s".toRegex()).filter { it.isNotEmpty() }
