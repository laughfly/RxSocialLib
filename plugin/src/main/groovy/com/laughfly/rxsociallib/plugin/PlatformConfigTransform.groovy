package com.laughfly.rxsociallib.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.*

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * Created by caowy on 2019/4/17.
 * email:cwy.fly2@gmail.com
 */

class PlatformConfigTransform extends Transform {

    Project project
    ConfigGenerator generator
    ClassPool classPool = ClassPool.default

    PlatformConfigTransform(Project project, ConfigGenerator generator) {
        this.project = project
        this.generator = generator
    }

    @Override
    String getName() {
        return "PlatformConfigTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs,
                          TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        inputs.each { TransformInput input ->
            println(getName() + ":TransformInput: " + input.toString())
            input.directoryInputs.each { DirectoryInput dirInput ->
                println(getName() + ":DirectoryInput: " + dirInput.file.getAbsolutePath())

                // 获取output目录
                def dest = outputProvider.getContentLocation(dirInput.name,
                        dirInput.contentTypes, dirInput.scopes,
                        Format.DIRECTORY)

                FileUtils.copyDirectory(dirInput.file, dest)
            }

            Set<String> classNameList = []
            JarInput libraryJar

            input.jarInputs.each { JarInput jarInput ->
                if (jarInput.name.contains("rxsociallib:library")) {
                    libraryJar = jarInput
                    classPool.insertClassPath(libraryJar.file.getAbsolutePath())
                } else if (jarInput.name.contains('rxsociallib')) {
                    classPool.insertClassPath(jarInput.file.getAbsolutePath())
                    traverseJar(jarInput.file, classNameList)
                    copyJar(jarInput, outputProvider)
                } else {
                    copyJar(jarInput, outputProvider)
                }
            }

            if (libraryJar != null){
                modifyLibraryJar(libraryJar, classNameList, context, outputProvider)
            }
        }
    }

    void modifyLibraryJar(JarInput libraryJar, Set<String> classNameList, Context context, TransformOutputProvider outputProvider) {

        def jarFile = libraryJar.file
        def tempDir = context.temporaryDir

        def newJarFile = new JarFile(jarFile)
        def hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8)
        def outputJar = new File(tempDir, hexName + jarFile.name)
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar))
        Enumeration enumeration = newJarFile.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            InputStream inputStream = newJarFile.getInputStream(jarEntry)

            String entryName = jarEntry.getName()

            ZipEntry zipEntry = new ZipEntry(entryName)

            jarOutputStream.putNextEntry(zipEntry)

            if (entryName.contains("SocialConfig.class")) {
                modifyConfigClass(classNameList, jarOutputStream)
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            jarOutputStream.closeEntry()
        }

        jarOutputStream.close()
        newJarFile.close()

        jarFile.delete()
        FileUtils.copyFile(outputJar, jarFile)

        copyJar(libraryJar, outputProvider)
    }

    void modifyConfigClass(Set<String> classNameList, JarOutputStream jarOutputStream) {
        def configClass = classPool.get("com.laughfly.rxsociallib.SocialConfig")

        def method = configClass.getDeclaredMethod("getSocialClassList")
        StringBuilder bodyBuilder = new StringBuilder()
        bodyBuilder.append("{java.util.List list = new java.util.ArrayList();")
        classNameList.each {
            println(it)
            bodyBuilder.append("list.add(${it});")
        }
        bodyBuilder.append("return list;}")

        method.setBody(bodyBuilder.toString())

        jarOutputStream.write(configClass.toBytecode())

        configClass.detach()
    }

    void traverseJar(File jarFile, Set<String> classNameList) {
        /**
         * 读取原jar
         */
        def file = new JarFile(jarFile)
        Enumeration enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            InputStream inputStream = file.getInputStream(jarEntry)

            String entryName = jarEntry.getName()
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)
            if (entryName.endsWith(".class")) {
                String className = entryName.replaceAll("/", ".")
                def finded = findClass(sourceClassBytes)
                if (finded) {
                    classNameList.add(className)
                }
            }
        }
        file.close()
    }

    boolean findClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor adapter = new ClassFilterVisitor(classWriter)
        ClassReader cr = new ClassReader(srcClass)
        cr.accept(adapter, 0)
        return adapter.finded
    }

    class ClassFilterVisitor extends ClassVisitor  implements Opcodes{

        boolean finded

        ClassFilterVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM5, classVisitor)
        }

        @Override
        AnnotationVisitor visitAnnotation(String s, boolean b) {
            println("ClassFilterVisitor: visitAnnotation: " + s)
            if ("Lcom/laughfly/rxsociallib/login/LoginFeatures;".equalsIgnoreCase(s) ||
                "Lcom/laughfly/rxsociallib/share/ShareFeatures;".equalsIgnoreCase(s)) {
                finded = true
            }
            return super.visitAnnotation(s, b)
        }

    }

    void copyJar(JarInput jarInput, TransformOutputProvider outputProvider) {
        def jarName = jarInput.name
        def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
//        if (jarName.endsWith(".jar")) {
//            jarName = jarName.substring(0, jarName.length() - 4)
//        }
        //生成输出路径
        def dest = outputProvider.getContentLocation(jarName + md5Name,
                jarInput.contentTypes, jarInput.scopes, Format.JAR)

        FileUtils.copyFile(jarInput.file, dest)
    }
}
