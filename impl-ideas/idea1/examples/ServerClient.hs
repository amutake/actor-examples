{-# LANGUAGE LambdaCase #-}

module ServerClient where

import Control.Concurrent
import Control.Concurrent.Actor
import Control.Monad.IO.Class
import qualified Data.IntMap as M
import System.Random

data Request = Create String
             | GetAll
             | Get Int
             | Delete Int
             | Update Int String

data Response = Ok
              | NotFound
              | Comment Int String
              | Comments [(Int, String)]

loggerDef :: Behavior String
loggerDef = Behavior $ liftIO . putStrLn

serverDef :: ActorId String -> Int -> M.IntMap String -> Behavior (Request, ActorId Response)
serverDef logger max map = Behavior $ \case
    (Create s, pid) -> do
        let map' = M.insert max s map
        logger ! "server: コメントを作成 (" ++ show max ++ ", " ++ s ++ ")"
        pid ! Comment max s
        become $ serverDef logger (max + 1) map'
    (GetAll, pid) -> do
        logger ! "server: すべてのコメント"
        pid ! Comments $ M.toList map
    (Get n, pid) -> do
        logger ! "server: コメント (" ++ show n ++ ")"
        let c = M.lookup n map
        maybe (pid ! NotFound) ((pid !) . Comment n) c
    (Delete n, pid) -> do
        logger ! "server: コメントを削除 (" ++ show n ++ ")"
        if M.member n map
            then do
                let map' = M.delete n map
                pid ! Ok
                become $ serverDef logger max map'
            else pid ! NotFound
    (Update n s, pid) -> do
        logger ! "server: コメントを更新 (" ++ show n ++ ", " ++ s ++ ")"
        if M.member n map
            then do
                let map' = M.insert n s map
                pid ! Comment n s
                become $ serverDef logger max map'
            else pid ! NotFound

clientDef :: ActorId String -> ActorId (Request, ActorId Response) -> Behavior Response
clientDef logger server = Behavior $ \case
    Ok -> do
        logger ! "client: OK"
        randomRequest
    NotFound -> do
        logger ! "client: NotFound"
        randomRequest
    Comment n s -> do
        logger ! "client: Comment (" ++ show n ++ ", " ++ s ++ ")"
        randomRequest
    Comments cs -> do
        logger ! "client: Comments (" ++ show cs ++ ")"
        randomRequest
  where
    randomRequest = do
        wait <- liftIO $ randomRIO (300000, 2000000 :: Int)
        liftIO $ threadDelay wait
        n <- liftIO $ randomRIO (1 :: Int, 5)
        me <- self
        case n of
            1 -> do
                gen <- liftIO newStdGen
                let s = take 20 $ randomRs ('a', 'z') gen
                server ! (Create s, me)
            2 -> server ! (GetAll, me)
            3 -> do
                m <- liftIO $ randomRIO (1, 20 :: Int)
                server ! (Get m, me)
            4 -> do
                m <- liftIO $ randomRIO (1, 20 :: Int)
                server ! (Delete m, me)
            5 -> do
                gen <- liftIO newStdGen
                let s = take 20 $ randomRs ('a', 'z') gen
                m <- liftIO $ randomRIO (1, 20 :: Int)
                server ! (Update m s, me)
            _ -> undefined

main :: IO ()
main = do
    logger <- new loggerDef
    server <- new $ serverDef logger 0 M.empty
    client1 <- new $ clientDef logger server
    client2 <- new $ clientDef logger server
    client3 <- new $ clientDef logger server
    client1 ! Ok
    threadDelay 300000
    client2 ! Ok
    threadDelay 300000
    client3 ! Ok
    threadDelay 1000000
