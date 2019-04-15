package com.loading.modules.compiler;

import com.loading.modules.annotation.Repeater;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;


@SuppressWarnings("unused")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ModuleProcessor extends AbstractProcessor {
    private static final String TAG = "ModuleProcessor";
    private static final String METHOD_INIT_FOR_CONFIG_CLASS = "initForClass";
    private ClassName moduleManagerName = ClassName.bestGuess("com.loading.modules.ModuleManager");
    private static final int DEFAULT_INT = -1;
    private static final String DEFAULT_OBJECT = "null";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        boolean isProcessed = false;
        if (isInitialized() && set != null && set.size() > 0) {
            //process Module
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Repeater.class);
            log("process() -> repeater elements : " + elements);
            for (Element element : elements) {
                TypeElement typeElement = (TypeElement) element;
                log("process repeater -> type element : " + typeElement);
                Repeater repeater = typeElement.getAnnotation(Repeater.class);
                String repeaterName = repeater.repeaterName();
                String packageName = repeater.repeaterPackage();
                if (!isStringValid(repeaterName)) {
                    repeaterName = getDefaultRepeaterName(typeElement);
                }
                if (!isStringValid(packageName)) {
                    packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
                }

                if (isStringValid(repeaterName) && isStringValid(packageName)) {
                    generateModuleMgrCode(repeaterName, packageName, typeElement);
                } else {
                    throw new IllegalStateException(String.format("repeater name : %s , package name : %s , type element : %s , repeater name and package name must not empty !", repeaterName, packageName, typeElement));
                }
            }

            isProcessed = true;
        }
        return isProcessed;
    }

    private void generateModuleMgrCode(String repeaterName, String packageName, TypeElement typeElement) {
        log("generateModuleMgrCode() -> repeater name : " + repeaterName + " , package name : " + packageName + " , typeElement : " + typeElement);
        ClassName targetClassName = ClassName.get(typeElement);
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(repeaterName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("Generated Code. Do not edit it!\n" +
                        "see annotation {@link $L}\n" +
                        "see interface {@link $L}\n", targetClassName.simpleName(), Repeater.class.getCanonicalName());
        List<? extends Element> tElements = typeElement.getEnclosedElements();
        for (Element methodElement : tElements) {
            if (methodElement instanceof Symbol.MethodSymbol) {
                Symbol.MethodSymbol tMethodElement = (Symbol.MethodSymbol) methodElement;
                List<Symbol.VarSymbol> tParameters = tMethodElement.getParameters();
                Type tReturnType = tMethodElement.getReturnType();
                String methodName = tMethodElement.getSimpleName().toString();
                MethodSpec.Builder methodBuilder = MethodSpec
                        .methodBuilder(methodName)
                        .returns(TypeName.get(tReturnType))
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
                ParameterSpec.Builder parameterBuilder;
                for (Symbol.VarSymbol tParameter : tParameters) {
                    parameterBuilder = ParameterSpec.builder(ClassName.get(tParameter.type), tParameter.toString());
                    methodBuilder.addParameter(parameterBuilder.build());
                }
                methodBuilder.addStatement("$T t$L = $T.get($T.class)", targetClassName, targetClassName.simpleName(), moduleManagerName, targetClassName);
                if (tReturnType.getKind() != TypeKind.VOID) {
                    if (tReturnType.getKind() == TypeKind.BOOLEAN) {
                        methodBuilder.addStatement("return t$L != null && t$L.$L($L)", targetClassName.simpleName(), targetClassName.simpleName(), methodName, tParameters);
                    } else if (tReturnType.getKind() == TypeKind.INT || tReturnType.getKind() == TypeKind.FLOAT || tReturnType.getKind() == TypeKind.LONG) {
                        methodBuilder.addStatement("return t$L != null ? t$L.$L($L) : $L",
                                targetClassName.simpleName(),
                                targetClassName.simpleName(),
                                methodName,
                                tParameters,
                                DEFAULT_INT);
                    } else {
                        methodBuilder.addStatement("return t$L != null ? t$L.$L($L) : $L",
                                targetClassName.simpleName(),
                                targetClassName.simpleName(),
                                methodName,
                                tParameters,
                                DEFAULT_OBJECT);
                    }
                } else {
                    methodBuilder
                            .beginControlFlow("if(t$L != null)", targetClassName.simpleName())
                            .addStatement("t$L.$L($L)", targetClassName.simpleName(), methodName, tParameters)
                            .endControlFlow();
                }
                typeBuilder.addMethod(methodBuilder.build());
            }
        }
        try {
            JavaFile.builder(packageName, typeBuilder.build())
                    .build()
                    .writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDefaultRepeaterName(TypeElement typeElement) {
        String simpleName = typeElement.getSimpleName().toString();
        log("getDefaultRepeaterName() -> type element : " + typeElement + " , simple name : " + simpleName);
        if (isStringValid(simpleName)) {
            final String prefix = "I";
            final String postFix = "Service";
            //only parse this format I***Service to ***ModuleMgr
            if (simpleName.startsWith(prefix) && simpleName.endsWith(postFix)) {
                String repeaterName = simpleName.substring(prefix.length(), simpleName.length() - postFix.length()) + "ModuleMgr";
                log("getDefaultRepeaterName() -> return repeater name : " + repeaterName);
                return repeaterName;
            } else {
                throw new IllegalStateException(String.format("only support pare this format I***Service, element : %s", typeElement));
            }
        } else {
            throw new IllegalStateException(String.format("type element get simple name is empty!! type element : %s", typeElement));
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Repeater.class.getCanonicalName());
        return types;
    }

    private boolean isStringValid(String s) {
        return s != null && s.length() > 0;
    }

    private void log(String msg) {
        System.out.println(String.format("%s - %s", TAG, msg));
    }
}
