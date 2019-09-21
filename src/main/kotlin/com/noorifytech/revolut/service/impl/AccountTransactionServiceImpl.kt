package com.noorifytech.revolut.service.impl

import com.noorifytech.revolut.dto.AccountTransactionDto
import com.noorifytech.revolut.dto.HttpStatusCode
import com.noorifytech.revolut.dto.Response
import com.noorifytech.revolut.exception.TransactionFailedException
import com.noorifytech.revolut.repository.AccountRepository
import com.noorifytech.revolut.repository.AccountTransactionRepository
import com.noorifytech.revolut.service.AccountTransactionService

class AccountTransactionServiceImpl(
        private val transactionRepository: AccountTransactionRepository,
        private val accountRepository: AccountRepository) : AccountTransactionService {

    override suspend fun getTransactions(): Response<List<AccountTransactionDto>> {
        val transactions = transactionRepository.get()
        return Response(transactions, HttpStatusCode.OK.value, HttpStatusCode.OK.description)
    }

    override suspend fun getTransaction(id: Int): Response<AccountTransactionDto> {
        val transaction = transactionRepository.get(id)
        return if (transaction == null) Response(null, HttpStatusCode.NotFound.value, HttpStatusCode.NotFound.description)
        else Response(transaction, HttpStatusCode.OK.value, HttpStatusCode.OK.description)
    }

    override suspend fun transfer(data: AccountTransactionDto): Response<AccountTransactionDto> {
        val srcAccount = accountRepository.get(data.srcAccountId)
        val destAccount = accountRepository.get(data.destAccountId)

        return when {
            srcAccount == null -> Response(null, HttpStatusCode.NotFound.value, "Source account not found!")
            destAccount == null -> Response(null, HttpStatusCode.NotFound.value, "Destination account not found!")
            else -> try {
                val transaction = transactionRepository.create(data)
                Response(transaction, HttpStatusCode.Created.value, "Transaction successfully completed!")
            } catch (ex: TransactionFailedException) {
                Response(null, HttpStatusCode.InternalServerError.value, "Transaction Failed due to ${ex.message}")
            }
        }
    }
}