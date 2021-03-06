package fhj.swengb.project.remoty

import java.io.BufferedReader
import java.nio.file._
import java.util
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.TreeItem
import javafx.scene.image.{Image, ImageView}

/**
  * Created by Amar on 22.01.2016.
  */
object PathTreeItem {

  /**
    * Creating a new TreeItem and checking if it is a leaf or has got children (=no Leaf)
    *
    * @param pi
    * @return
    */
  def createNode(pi: PathItem): TreeItem[PathItem] = {
    new TreeItem[PathItem](pi){
      var isLeaf1: Boolean = _
      var isFirstTimeChildren: Boolean = true
      var isFirstTimeLeaf = true



      override def getChildren: ObservableList[TreeItem[PathItem]] = {
        if(isFirstTimeChildren){
          isFirstTimeChildren = false
          super.getChildren.setAll(buildChildren(this))
        }
        super.getChildren
      }


      override def isLeaf: Boolean = {
        if(isFirstTimeLeaf){
          isFirstTimeLeaf = false
          isLeaf1 = Files.isRegularFile(this.getValue.getPath, LinkOption.NOFOLLOW_LINKS)
        }
        isLeaf1
      }

      /**
        * creates new TreeItems and its childs if it is not a Leaf
        * @param treeItem
        * @return
        */
      def buildChildren(treeItem: TreeItem[PathItem]): ObservableList[TreeItem[PathItem]] = {
        //creating root TreeItem
        val path:Path = treeItem.getValue.getPath

        if(path != null && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)){
          val directories:ObservableList[TreeItem[PathItem]] = FXCollections.observableArrayList()
          val files:ObservableList[TreeItem[PathItem]] = FXCollections.observableArrayList()
          val result:ObservableList[TreeItem[PathItem]] = FXCollections.observableArrayList()

          //ruft die Funktion vom File "TreeViewUtil" auf und wandelt einen DirectoryStream in eine Liste um
          val dirs = TreeViewUtil.stringer(Files.newDirectoryStream(path))


          for(dir: Path <- dirs){
            if(!Files.isHidden(dir) && Files.isReadable(dir)) {
              val child = createNode(new PathItem(dir))
              if (Files.isRegularFile(dir, LinkOption.NOFOLLOW_LINKS)) {
                files.add(child)

              }

              else {
                directories.add(child)
              }

            }
          }
          result.addAll(directories)
          result.addAll(files)
          return result
        }
        FXCollections.emptyObservableList()
      }
    }
  }





}
