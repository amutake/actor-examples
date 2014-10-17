{-# LANGUAGE LambdaCase #-}

module VendingMachine where

import Control.Concurrent
import Control.Concurrent.Actor
import Control.Monad.IO.Class
import System.Random

data Item = Tea | Coffee | CoinIndicator Int
data Control = PutCoin | TeaButton | CoffeeButton

loggerBehavior :: Behavior String
loggerBehavior = Behavior $ liftIO . putStrLn

vendingMachineBehavior :: ActorId String -> Int -> Behavior (Control, ActorId Item)
vendingMachineBehavior logger coin = Behavior $ \case
    (PutCoin, customer) -> do
        logger ! "コインが投入されました"
        customer ! CoinIndicator $ coin + 1
        become $ vendingMachineBehavior logger $ coin + 1
    (TeaButton, customer) -> do
        logger ! "お茶ボタンが押されました"
        if coin >= 1
            then do
                logger ! "お茶を出しました"
                customer ! Tea
                customer ! CoinIndicator $ coin - 1
                become $ vendingMachineBehavior logger $ coin - 1
            else do
                logger ! "コインが足りません"
                customer ! CoinIndicator coin
    (CoffeeButton, customer) -> do
        logger ! "コーヒーボタンが押されました"
        if coin >= 1
            then do
                logger ! "コーヒーを出しました"
                customer ! Coffee
                customer ! CoinIndicator $ coin - 1
                become $ vendingMachineBehavior logger $ coin - 1
            else do
                logger ! "コインが足りません"
                customer ! CoinIndicator coin

customerBehavior :: ActorId String -> ActorId (Control, ActorId Item) -> Behavior Item
customerBehavior logger vm = Behavior $ \case
    Tea -> logger ! "お茶を買いました"
    Coffee -> logger ! "コーヒーを買いました"
    CoinIndicator coin -> do
        logger ! "現在投入されているコインは" ++ show coin ++ "枚らしいです"
        me <- self
        n <- liftIO $ (randomRIO (1, 4) :: IO Int)
        liftIO $ threadDelay 300000
        case n of
            1 -> vm ! (PutCoin, me)
            2 -> vm ! (PutCoin, me)
            3 -> vm ! (TeaButton, me)
            4 -> vm ! (CoffeeButton, me)
            _ -> undefined

main :: IO ()
main = do
    logger <- new loggerBehavior
    vm <- new $ vendingMachineBehavior logger 0
    customer <- new $ customerBehavior logger vm
    customer ! CoinIndicator 0
    threadDelay 10000
