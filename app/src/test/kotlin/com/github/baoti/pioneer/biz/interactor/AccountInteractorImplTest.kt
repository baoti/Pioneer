package com.github.baoti.pioneer.biz.interactor;

import android.net.Uri
import com.github.baoti.pioneer.data.api.AccountApi
import com.github.baoti.pioneer.data.prefs.AccountPrefs
import com.github.baoti.pioneer.entity.Account
import com.github.baoti.pioneer.event.AccountChangedEvent
import com.github.baoti.pioneer.event.EventPoster
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Ignore
import org.mockito.Mockito.*

class AccountInteractorImplTest {

    lateinit var impl: AccountInteractor

    private val poster = mock(EventPoster::class.java)
    private val accountApi = mock(AccountApi::class.java)
    private val accountPrefs = mock(AccountPrefs::class.java)

    @Before
    fun setUp() {
        impl = AccountInteractorImpl(poster, accountApi, accountPrefs)
    }

    @Test
    fun hasAccount_hasAnonymous() {
        assertTrue(impl.hasAccount())
    }

    @Test
    fun getAccount_getAnonymous() {
        assertSame(Account.ANONYMOUS, impl.account)

        assertSame(Account.ANONYMOUS.accountId, impl.accountId)
    }

    @Test
    fun changeAvatar() {
        assertNull(impl.account.avatar)

        val uri = mock(Uri::class.java)

        impl.changeAvatar(uri)

        assertSame(uri, impl.account.avatar.url)
        assertNotSame(Account.ANONYMOUS, impl.account)
        assertSame(Account.ANONYMOUS.accountId, impl.accountId)

        verify(poster).postOnBoth(any(AccountChangedEvent::class.java))
        verifyNoMoreInteractions(poster, accountApi, accountPrefs)
    }

    @Test
    fun signOut_noAnonymous() {
        assertTrue(impl.hasAccount())

        impl.signOut()

        assertFalse(impl.hasAccount())

        verify(poster).postOnBoth(any(AccountChangedEvent::class.java))
        verify(accountPrefs).clear()

        verifyNoMoreInteractions(poster, accountApi, accountPrefs)
    }

    @Ignore // TODO test signInDeferred
    @Test
    fun signIn() {
        throw UnsupportedOperationException()
    }
}