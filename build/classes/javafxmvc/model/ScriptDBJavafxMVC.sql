-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema javafxmvc
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema javafxmvc
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `javafxmvc` DEFAULT CHARACTER SET utf8 ;
USE `javafxmvc` ;

-- -----------------------------------------------------
-- Table `javafxmvc`.`categorias`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `javafxmvc`.`categorias` (
  `idCategoria` INT(11) NOT NULL AUTO_INCREMENT,
  `descricao` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`idCategoria`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `javafxmvc`.`clientes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `javafxmvc`.`clientes` (
  `idCliente` INT(11) NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(50) NOT NULL,
  `cpf` VARCHAR(50) NOT NULL,
  `telefone` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`idCliente`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `javafxmvc`.`produtos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `javafxmvc`.`produtos` (
  `idProduto` INT(11) NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(50) NOT NULL,
  `preco` FLOAT NOT NULL,
  `quantidade` INT(11) NOT NULL,
  `idCategoria` INT(11) NOT NULL,
  PRIMARY KEY (`idProduto`),
  INDEX `fk_produtos_categorias` (`idCategoria` ASC) VISIBLE,
  CONSTRAINT `fk_produtos_categorias`
    FOREIGN KEY (`idCategoria`)
    REFERENCES `javafxmvc`.`categorias` (`idCategoria`))
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `javafxmvc`.`vendas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `javafxmvc`.`vendas` (
  `idVenda` INT(11) NOT NULL AUTO_INCREMENT,
  `data` DATE NOT NULL,
  `valor` FLOAT NOT NULL,
  `pago` TINYINT(1) NOT NULL,
  `idCliente` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`idVenda`),
  INDEX `fk_vendas_clientes` (`idCliente` ASC) VISIBLE,
  CONSTRAINT `fk_vendas_clientes`
    FOREIGN KEY (`idCliente`)
    REFERENCES `javafxmvc`.`clientes` (`idCliente`))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `javafxmvc`.`itensdevenda`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `javafxmvc`.`itensdevenda` (
  `idItemDeVenda` INT(11) NOT NULL AUTO_INCREMENT,
  `quantidade` INT(11) NOT NULL,
  `valor` FLOAT NOT NULL,
  `idProduto` INT(11) NULL DEFAULT NULL,
  `idVenda` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`idItemDeVenda`),
  INDEX `fk_itensdevenda_produtos` (`idProduto` ASC) VISIBLE,
  INDEX `fk_itensdevenda_vendas` (`idVenda` ASC) VISIBLE,
  CONSTRAINT `fk_itensdevenda_produtos`
    FOREIGN KEY (`idProduto`)
    REFERENCES `javafxmvc`.`produtos` (`idProduto`),
  CONSTRAINT `fk_itensdevenda_vendas`
    FOREIGN KEY (`idVenda`)
    REFERENCES `javafxmvc`.`vendas` (`idVenda`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
