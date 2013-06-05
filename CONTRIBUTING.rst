=================
How to Contribute
=================

We welcome community contributions to the XIP(tm) project.
Your contributions back to XIP will allow the broader
community to benefit from your work and will allow your
enhancements to be integrated with those of others.  There are a few
guidelines that we ask contributors to follow so that we can have a
chance of keeping on top of things.

---------------
Getting Started
---------------

* Make sure you have a `GitHub Account`_.

* Fork the repository on GitHub to publish any proposed changes.

* Optionally submit a ticket for your issue at <https://plans.imphub.org/browse/XIP>,
  or add a comment to an existing one.

  - Get an _`imphub Account` to create or edit an issue.
  - Clearly describe the issue including steps to reproduce when it is a bug.
  - Make sure you fill in the earliest version that you know has the issue.
  - Include a comment indicating that you would like to work on the issue.

.. _`GitHub Account`: https://github.com/signup/free
.. _`imphub Account`: https://plans.imphub.org/secure/Signup!default.jspa

--------------
Making Changes
--------------

* Create a topic branch from where you want to base your work.

  - This is usually the master branch.
  - Only target release branches if you are certain your fix must be
    on that branch.
  - To quickly create a topic branch based on master::

     git checkout -b fix/master/my_contribution master

    Please avoid working directly on the master branch.

* Make commits of logical units.

* Please provide tests and documentation with your changes.

* Check for unnecessary whitespace with ``git diff --check`` before committing.

* Follow the `NCIP Good Practices for Commit Messages`_.
  Start with a one-line summary followed by a blank line followed by a
  detailed free-form description.

.. _`NCIP Good Practices for Commit Messages`: https://github.com/NCIP/ncip.github.com/wiki/Good-Practices#wiki-commit-messages

------------------
Submitting Changes
------------------

* In general, we require that the OSI-approved `Apache 2.0 License`_
  be applied to code contributions.  There may be cases, however, that
  warrant the use of an alternate OSI-approved license and we will
  evaluate this situations case-by-case.

* Push your changes to a topic branch in your fork of the repository.  Make sure 
  the public can read (pull) from your repository!

* Submit a pull request to the repository in the OpenXIP organization.

* Update your <https://plans.imphub.org/browse/XIP> ticket to mark that you have submitted
  code and are ready for it to be reviewed.

  - Include a link to the pull request in the ticket

  - It would be nice, but very optional, if you also recorded the time you spent on the issue

.. _`Apache 2.0 License`: http://opensource.org/licenses/Apache-2.0

------------------
What Happens Next?
------------------

Members of the XIP Committers team will review your proposed changes, and possibly suggest 
changes or improvements.  They will pull accepted changes back into the master XIP repository
on `imphub`_, which will automatically mirror the changes onto GitHub.

.._`imphub`: https://code.imphub.org/projects/XIP

---------------------------------
How Do I Become an XIP Committer?
---------------------------------

Just ask!  We are always looking for good people who are willing to dedicate some time 
towards improving XIP(tm).  Since a majority vote of the existing XIP committers is needed
to become a committer, you may need to prove your worth first, e.g. by contributing a few 
changes or enhancements using the above process.

--------------------
Additional Resources
--------------------

For help learning Git and Github, see the `NCIP Learning Resources`_.

.. _`NCIP Learning Resources`: https://github.com/NCIP/ncip.github.com/wiki/Learning-Resources
