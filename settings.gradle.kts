rootProject.name = "ul4"

buildCache {
	local {
		directory = File(rootDir, ".build-cache")
		removeUnusedEntriesAfterDays = 30
	}
}
