package com.app.apt.processor;

import com.app.annotation.apt.Router;
import com.app.apt.AnnotationProcessor;
import com.app.apt.helper.RouterActivityModel;
import com.app.apt.inter.IProcessor;
import com.app.apt.util.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.FilerException;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Created by wangshizhan on 17/07/28.
 * 2.7.6版本为单module
 * 3.0以后版本支持多module
 * 视图跳转路由器
 */

public class MultiRouterProcessor implements IProcessor {
    @Override
    public void process(RoundEnvironment roundEnv, AnnotationProcessor mAbstractProcessor) {
        String CLASS_NAME = "IntentRouter";
        String PACKAGE_NAME =null;

        TypeSpec.Builder tb = classBuilder(CLASS_NAME)
                .addModifiers(PUBLIC, FINAL)
                .addSuperinterface(ClassName.get("com.supcon.common.com_router.api","IRouter"))
                .addJavadoc("@API intent router created by apt\n" +
                "支持组件化多模块\n" +
                "add by wangshizhan\n");



        CodeBlock.Builder staticBuilderGo = CodeBlock.builder();
        ClassName routerManagerClassName = ClassName.get("com.supcon.common.com_router.util", "RouterManager");

        MethodSpec.Builder methodBuilder1 = MethodSpec.methodBuilder("go")
                .addJavadoc("@created by apt \n")
                .addModifiers(PUBLIC, STATIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addParameter(String.class, "name")
                .addParameter(ClassName.get("android.os", "Bundle"), "extra")
                ;

        List<ClassName> mList = new ArrayList<>();
        CodeBlock.Builder blockBuilderGo = CodeBlock.builder();
        ClassName mIntentClassName = ClassName.get("android.content", "Intent");

        blockBuilderGo.add("$L intent =" +
                        "new $L();\n",
                mIntentClassName,
                mIntentClassName
        );

        blockBuilderGo.add("if(extra != null)\n");
        blockBuilderGo.add("\t\tintent.putExtras(extra);\n");
        blockBuilderGo.beginControlFlow(" switch (name)");//括号开始

        List<RouterActivityModel> mRouterActivityModels = new ArrayList<>();
        try {
            for (TypeElement element : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(Router.class))) {
                ClassName currentType = ClassName.get(element);
                if (mList.contains(currentType)) continue;
                mList.add(currentType);
                RouterActivityModel mRouterActivityModel = new RouterActivityModel();
                mRouterActivityModel.setElement(element);
                mRouterActivityModel.setActionName(element.getAnnotation(Router.class).value());

                mRouterActivityModel.setNeedBind(false);
                mRouterActivityModels.add(mRouterActivityModel);

                if(PACKAGE_NAME == null) {
                    String temp = element.getQualifiedName().toString();
                    temp = temp.substring(0, temp.lastIndexOf("."));
                    PACKAGE_NAME = temp.substring(0, temp.lastIndexOf("."));
                }
            }

            if(mRouterActivityModels.size() ==0){
                return;
            }

            for (RouterActivityModel item : mRouterActivityModels) {
                blockBuilderGo.add("case $S: \n", item.getActionName());
                blockBuilderGo.add("intent.setClass(context, $L.class);\n", item.getElement());
                blockBuilderGo.addStatement("\nbreak");

                staticBuilderGo.addStatement("$L.getInstance().register(\"$L\", $L.class)",
                        routerManagerClassName,
                        item.getActionName(),
                        item.getElement().getQualifiedName()
                );
            }
            blockBuilderGo.add("default: \n");

            blockBuilderGo.add("$L routerManager = $L.getInstance();\n", routerManagerClassName, routerManagerClassName);
            blockBuilderGo.add("Class destinationClass = routerManager.getDestination(name);\n");
            blockBuilderGo.addStatement("if(destinationClass == null) return");
            blockBuilderGo.addStatement(
                    "intent.setClass(context, destinationClass);\n"+
                    "break");

            blockBuilderGo.endControlFlow();
            blockBuilderGo.addStatement("context.startActivity(intent)"
            );
            methodBuilder1.addCode(blockBuilderGo.build());

            tb.addStaticBlock(staticBuilderGo.build());
            tb.addMethod(methodBuilder1.build());

            tb.addMethod(MethodSpec.methodBuilder("go")
                    .addJavadoc("@created by apt")
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(ClassName.get("android.content", "Context"), "context")
                    .addParameter(String.class, "name")
                    .addCode("go(context, name, null);\n").build());

            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, tb.build()).build();// 生成源代码
            javaFile.writeTo(mAbstractProcessor.mFiler);// 在 app module/build/generated/source/apt 生成一份源代码
        } catch (FilerException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
