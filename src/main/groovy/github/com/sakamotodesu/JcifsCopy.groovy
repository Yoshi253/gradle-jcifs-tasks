package github.com.sakamotodesu

import github.com.sakamotodesu.file.CopyFileFactory
import jcifs.Config
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskAction

/**
 * task for copying cifs file
 */
class JcifsCopy extends DefaultTask {

    String from
    String into
    String lmCompatibility = 3
    String include
    String exclude
    String cleanBefore = null
    String cleanAfter = null

    @TaskAction
    def jcifsCopy() {
        Config.setProperty("jcifs.smb.lmCompatibility", lmCompatibility);
        if (from == null || from.isEmpty()) {
            throw new InvalidUserDataException("from is empty")
        }
        if (into == null || into.isEmpty()) {
            throw new InvalidUserDataException("into is empty")
        }

        def src = CopyFileFactory.get(from)
        def dst = CopyFileFactory.get(into)

        if (cleanBefore != null) {
            def cleanBeforeDir = CopyFileFactory.get(cleanBefore)
            cleanBeforeDir.deleteDirectoryContents()
        }

        if (!dst.exists()) {
            dst.mkdirs()
        }

        src.getFileList().findAll {
            if (include == null || include.isEmpty()) {
                true
            } else {
                it.getName().matches(include)
            }
        }.findAll {
            if (exclude == null || exclude.isEmpty()) {
                true
            } else {
                !it.getName().matches(exclude)
            }
        }.each {
            def dstFile = CopyFileFactory.get(dst, it.getName())
            it.copyTo(dstFile)
        }

        if (cleanAfter != null) {
            def cleanAfterDir = CopyFileFactory.get(cleanAfter)
            cleanAfterDir.deleteDirectoryContents()
        }
    }

}
