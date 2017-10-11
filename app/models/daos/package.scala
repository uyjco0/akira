package models

import slick.jdbc.JdbcBackend.Database

package object daos {
  val db = Database.forConfig("akiradb")  
}
