package com.sunshine.scala

import scala.io.Source
import java.io.File

/**
 * Created by zyj on 14-7-18.
 */


class RichFile (val file : File){
  def read = Source.fromFile(file.getPath).mkString
}

class ImplictPaire[T : Ordering](val first : T , val second : T){
  def smaller(implicit ord: Ordering[T]) = {
    if (ord.compare(first,second) < 0) first else second
  }
}

trait LineOrdering extends Ordering[Line] {
  override def compare(x : Line, y : Line) : Int = {
    if(x.len < y.len) -1
    else if(x.len == y.len) 0
    else 1
  }
}

class Line(val len : Double){
  override def toString() = "Length of line :" + len
}

object Context{
  implicit def file2RichFile(f : File) = new RichFile(f)

  implicit val prefix : String = "Hello"

  //隐式参数 转换
  def smaller[T](a : T , b : T)(implicit order : T => Ordered[T]) = if (a < b) a else b

  implicit class ImplictClass(x : Int){
    def add = x + 2
  }
}

object ImplictParam{
  def print(content : String)(implicit prefix : String){
    println(prefix + ":" + content)
  }
}


object ImplictDemo extends  App{
  import Context._
  implicit object Line extends LineOrdering
  //println(new File("/Users/zyj/Downloads/my.cnf").read)

  //没有隐式转换
  //ImplictParam.print("Jack")("Hello ")

  //使用隐式转换
  ImplictParam.print("Jack")

  println(1.add)

  val p1 = new ImplictPaire("A","B")
  val p2 = new ImplictPaire(2,3)
  println(p1.smaller)
  println(p2.smaller)

  val l1 = new Line(1)
  val l2 = new Line(2)
  val p3 = new ImplictPaire[Line](l1, l2)

  println(p3.smaller)
}